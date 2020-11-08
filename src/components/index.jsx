/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:58
 */
// --------index.jsx--------
import "../assets/css/App.css";

import React, { Component } from "react";
import { Container } from "react-bootstrap";
import { connect, Provider } from "react-redux";
import { addLocalPref, getLocalPref } from "../mq/actions/navigation/Common/Side/Settings";
import Login from "./Login";
import Navigation from "./Navigation";
import Chat from "./Chat";
import LoadData from "./Common/Spinner";

const mapStateToProps = state => {
	return state;
};
const mapDispatchToProps = dispatch => {
	return {
		getLocalPref: obj => dispatch(getLocalPref(obj)),
		addLocalPref: obj => dispatch(addLocalPref(obj)),
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	class App extends Component {
		// Before show component load configuration from local storage
		componentDidMount() {
			const { defaultParams, getLocalPref } = this.props;
			const { prefixStorageKey, postfixStorageKey } = defaultParams.constants.sideSettings;
			getLocalPref({ key: `${prefixStorageKey}User${postfixStorageKey}` });
		}

		// If some settings change then it is saving of new data configuration to local storage
		componentDidUpdate(prevProps) {
			const { user, mode, darkLightChecked, leftRightChecked, nav_side, defaultParams, addLocalPref } = this.props;
			if (
				prevProps.user !== user ||
				prevProps.mode !== mode ||
				prevProps.darkLightChecked !== darkLightChecked ||
				prevProps.leftRightChecked !== leftRightChecked ||
				prevProps.nav_side !== nav_side
			) {
				const { prefixStorageKey, postfixStorageKey } = defaultParams.constants.sideSettings;
				addLocalPref({
					key: `${prefixStorageKey}User${postfixStorageKey}`,
					data: {
						user,
						mode,
						darkLightChecked,
						leftRightChecked,
						nav_side,
					},
				});
			}
		}

		/**
		 * Creating "Login" component for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_renderLogin() {
			const { mode, defaultParams, store, user } = this.props;
			return (
				<div className={`app-main-logo ${mode}`}>
					<Provider store={store.user}>
						<Login
							{...{
								mode,
								defaultParams,
								store,
								oldUser: user,
							}}
						/>
					</Provider>
				</div>
			);
		}

		/**
		 * Creating "InProgress" component for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_renderInProgress() {
			const { strings, mode } = this.props;
			return (
				<LoadData
					{...{
						mode: mode === "light" ? "dark" : "light",
						text: strings.load,
						size: "lg",
						classNames: "absolute",
					}}
				/>
			);
		}

		/**
		 * Creating "Navigation" and "Chat" components for render its
		 * @returns {JSX.Element}
		 * @private
		 */
		_renderMain() {
			const { store, defaultParams, mode, nav_side, user, lastChat } = this.props;

			const { navigation, chat } = store;
			return (
				<>
					<Provider store={navigation}>
						<Navigation
							{...{
								defaultParams,
								store,
								mode,
								user,
								lastChat,
								side_names: nav_side,
							}}
						/>
					</Provider>
					<Provider store={chat}>
						<Chat
							{...{
								store,
								mode,
								user,
							}}
						/>
					</Provider>
				</>
			);
		}

		render() {
			const { mode, user, loadSettings } = this.props;
			return (
				<Container fluid id="App" className={`main-container ${mode}`}>
					{loadSettings ? this._renderInProgress() : user.endTTL ? this._renderLogin() : this._renderMain()}
				</Container>
			);
		}
	}
);
