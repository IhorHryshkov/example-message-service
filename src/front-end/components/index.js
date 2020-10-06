/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:58
 */
//--------index.js--------
import '../assets/css/App.css';

import React, {Component}           from 'react';
import {Container}                  from 'react-bootstrap';
import {connect, Provider}          from "react-redux";
import {addLocalPref, getLocalPref} from "../mq/actions/navigation/Common/Side/Settings";
import Login                        from './Login';
import Navigation                   from './Navigation';
import Chat                         from './Chat';
import LoadData                     from './Common/Spinner';

const mapStateToProps    = (state) => {
	return state;
};
const mapDispatchToProps = (dispatch) => {
	return {
		getLocalPref: obj => dispatch(getLocalPref(obj)),
		addLocalPref: obj => dispatch(addLocalPref(obj))
	};
};

class App extends Component {
	componentDidMount() {
		const {prefixStorageKey, postfixStorageKey} = this.props.defaultParams.constants.sideSettings;
		this.props.getLocalPref({key: `${prefixStorageKey}User${postfixStorageKey}`});
	}

	componentDidUpdate(prevProps, prevState, snapshot) {
		const {user, mode, darkLightChecked, leftRightChecked, nav_side} = this.props;
		if (
			prevProps.user !== user || prevProps.mode !== mode ||
			prevProps.darkLightChecked !== darkLightChecked ||
			prevProps.leftRightChecked !== leftRightChecked ||
			prevProps.nav_side !== nav_side
		) {
			const {prefixStorageKey, postfixStorageKey} = this.props.defaultParams.constants.sideSettings;
			this.props.addLocalPref({
				key : `${prefixStorageKey}User${postfixStorageKey}`,
				data: {
					user,
					mode,
					darkLightChecked,
					leftRightChecked,
					nav_side
				}
			});
		}
	}

	_renderLogin() {
		const {mode, defaultParams, store, user} = this.props;
		return <div className={`app-main-logo ${mode}`}>
			<Provider store={store.user}>
				<Login
					{...{
						mode,
						defaultParams,
						store,
						oldUser: user
					}}
				/>
			</Provider>
		</div>;
	}

	_renderInProgress() {
		const {strings, mode} = this.props;
		return <LoadData {...{
			mode      : mode === 'light' ? 'dark' : 'light',
			text      : strings.load,
			size      : "lg",
			classNames: "absolute"
		}}/>;
	}

	_renderMain() {
		const {store, defaultParams, mode, nav_side, user} = this.props;
		const {navigation, chat}                           = store;
		return <>
			<Provider store={navigation}>
				<Navigation {...{
					defaultParams,
					store,
					mode,
					username  : user.username,
					side_names: nav_side
				}}/>
			</Provider>
			<Provider store={chat}>
				<Chat {...{mode}}/>
			</Provider>
		</>;
	}

	render() {
		const {mode, user, loadSettings} = this.props;
		return (
			<Container fluid id="App" className={`main-container ${mode}`}>
				{loadSettings ? this._renderInProgress() : user.endTTL ? this._renderLogin() : this._renderMain()}
			</Container>
		);
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	App);
