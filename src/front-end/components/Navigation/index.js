/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:57
 */
//--------index.js--------
import '../../assets/css/components/navigation/Navigation.css';
import React, {Component}  from 'react';
import {connect, Provider} from "react-redux";
import Index               from './Common/Side';
import SideSettings        from './Common/Side/Settings';

import {
	allClickMenu,
	allKeyMenu,
	selectItemMenu,
	touchEndMenu,
	touchMoveMenu,
	touchStartMenu
}                from "../../mq/actions/navigation";
import SideUsers from './Common/Side/Users';

const mapDispatchToProps = (dispatch) => {
	return {
		allClickMenu  : obj => dispatch(allClickMenu(obj)),
		allKeyMenu    : key => dispatch(allKeyMenu(key)),
		touchStartMenu: x => dispatch(touchStartMenu(x)),
		touchMoveMenu : x => dispatch(touchMoveMenu(x)),
		touchEndMenu  : obj => dispatch(touchEndMenu(obj)),
		selectItemMenu: obj => dispatch(selectItemMenu(obj))
	};
};

const mapStateToProps = (state) => {
	return state;
};

class Navigation extends Component {
	constructor(props) {
		super(props);
		this._handleClick  = this._handleClick.bind(this);
		this._handleKey    = this._handleKey.bind(this);
		this._onTouchStart = this._onTouchStart.bind(this);
		this._onTouchMove  = this._onTouchMove.bind(this);
		this._onTouchEnd   = this._onTouchEnd.bind(this);
	}

	componentDidMount() {
		document.addEventListener(
			'touchstart',
			this._onTouchStart,
			false
		);
		document.addEventListener(
			'touchmove',
			this._onTouchMove,
			false
		);
		document.addEventListener(
			'touchend',
			this._onTouchEnd,
			false
		);
		document.addEventListener(
			'click',
			this._handleClick,
			false
		);
		document.addEventListener(
			'keydown',
			this._handleKey,
			false
		);
	}


	componentWillUnmount() {
		document.removeEventListener(
			'touchstart',
			this._onTouchStart
		);
		document.removeEventListener(
			'touchmove',
			this._onTouchMove
		);
		document.removeEventListener(
			'touchend',
			this._onTouchEnd
		);
		document.removeEventListener(
			'click',
			this._handleClick
		);
		document.removeEventListener(
			'keydown',
			this._handleKey
		)
	}

	_onTouchStart(e) {
		const touch = e.changedTouches[0];
		this.props.touchStartMenu(touch.clientX);

	}

	_onTouchMove(e) {
		if (e.changedTouches && e.changedTouches.length) {
			this.props.touchMoveMenu(e.changedTouches[0].clientX);
		}
	}

	_onTouchEnd() {
		this.props.touchEndMenu({
			startX          : this.props.swipe.startX,
			endX            : this.props.swipe.endX,
			swiping         : this.props.swipe.swiping,
			minSwipeDistance: this.props.minSwipeDistance,
			left            : this.props.values.pref.nav_side.left,
			right           : this.props.values.pref.nav_side.right
		});
	}

	_handleKey(e) {
		this.props.allKeyMenu(e.key);
	}

	_handleClick(e) {
		const path = e.path || (e.composedPath && e.composedPath()) || [e.target];

		const {settings, users} = this.props.defaultParams.constants.navigation.buttonId;

		this.props.allClickMenu({
			path        : path[0],
			excludeClose: this.props.excludeClose,
			buttonIds   : [
				settings,
				users
			],
			nav_side    : {
				[settings]: {show: this.props.nav_side[settings].show},
				[users]   : {show: this.props.nav_side[users].show}
			}
		});
	}

	_loadSideMenu(side) {
		const {side_names, mode} = this.props;
		const menuName           = side_names[side];
		return <Index
			values={{
				side,
				mode,
				body      : this[`_initSide_${menuName}`](),
				show      : this.props.nav_side[menuName].show,
				buttonId  : this.props.nav_side[menuName].buttonId,
				strings   : {
					up  : this.props.strings.up,
					down: this.props.strings.down,
					side: this.props.strings[menuName]
				},
				headerName: this.props.strings[this.props.nav_side[menuName].headerName]
			}}
		/>
	}

	_initSide_settings() {
		const {app, username} = this.props.store;
		return (
			<Provider store={app}>
				<SideSettings {...{
					user           : {username},
					settings_titles: this.props.strings.settings_titles
				}}/>
			</Provider>
		);
	}

	_initSide_users() {
		const {side} = this.props.store;
		return (
			<Provider store={side.users}>
				<SideUsers {...{mode: this.props.mode}} store={this.props.store}/>
			</Provider>
		);
	}

	render() {
		return (
			<>
				{this._loadSideMenu('right')}
				{this._loadSideMenu('left')}
			</>
		);
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	Navigation);

