/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
import "./assets/css/all.css";

import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "react-redux";
import Dexie from "dexie";
import App from "./components";
import appInit from "./mq/store";
import sideUsersInit from "./mq/store/navigation/Common/Side/Chats";
import chatInit from "./mq/store/chat";
import userInit from "./mq/store/user";
import navigationStore from "./mq/store/navigation";
import messages from "./config/messages.json";
import DatabaseConfigImpl from "./database";
import NetworkConfigImpl from "./network";
import { constants } from "./config/constants.json";

const main = async () => {
	const defaultParams = {
		messages,
		constants,
	};
	const dbConfig = new DatabaseConfigImpl({
		defaultParams,
		config: {
			local: localStorage,
			session: sessionStorage,
			indexDB: new Dexie(constants.global.database.name),
		},
	});
	const netConfig = new NetworkConfigImpl({
		defaultParams,
		config: constants.global.network,
	});

	await netConfig.startSockConnect(netConfig.getSockService(), {}, "server");
	const headers = {
		...(process.env.REACT_APP_SOCKET_PROXY_LOGIN && { login: process.env.REACT_APP_SOCKET_PROXY_LOGIN }),
		...(process.env.REACT_APP_SOCKET_PROXY_PASS && { passcode: process.env.REACT_APP_SOCKET_PROXY_PASS }),
	};
	await netConfig.startSockConnect(netConfig.getSockProxy(), headers, "proxy");

	const app = appInit({
		netConfig,
		dbConfig,
		defaultParams,
	});
	const sideUsers = sideUsersInit({
		netConfig,
		dbConfig,
		defaultParams,
	});
	const user = userInit({
		netConfig,
		dbConfig,
		defaultParams,
	});
	const chat = chatInit({
		netConfig,
		dbConfig,
		defaultParams,
	});

	window.addEventListener("resize", () => {
		const vh = window.innerHeight * 0.01;
		document.documentElement.style.setProperty("--vh", `${vh}px`);
	});
	const vh = window.innerHeight * 0.01;
	document.documentElement.style.setProperty("--vh", `${vh}px`);

	ReactDOM.render(
		<Provider store={app}>
			<App
				config={{
					dbConfig,
					netConfig,
				}}
				store={{
					chat,
					user,
					app,
					navigation: navigationStore,
					side: {
						chats: sideUsers,
					},
				}}
				defaultParams={defaultParams}
			/>
		</Provider>,
		document.getElementById("root")
	);
};
main();
