/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:57
 */
// --------index.jsx--------
import "../../assets/css/components/navigation/Navigation.css";
import React, { Component } from "react";
import { connect, Provider } from "react-redux";
import Side from "./Common/Side";
import SideSettings from "./Common/Side/Settings";
import SideUsers from "./Common/Side/Chats";

import { allClickMenu, allKeyMenu, touchEndMenu, touchMoveMenu, touchStartMenu } from "../../mq/actions/navigation";

const mapDispatchToProps = dispatch => {
	return {
		allClickMenu: obj => dispatch(allClickMenu(obj)),
		allKeyMenu: key => dispatch(allKeyMenu(key)),
		touchStartMenu: x => dispatch(touchStartMenu(x)),
		touchMoveMenu: x => dispatch(touchMoveMenu(x)),
		touchEndMenu: obj => dispatch(touchEndMenu(obj)),
	};
};

const mapStateToProps = state => {
	return state;
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	class Navigation extends Component {
		constructor(props) {
			super(props);
			this._handleClick = this._handleClick.bind(this);
			this._handleKey = this._handleKey.bind(this);
			this._onTouchStart = this._onTouchStart.bind(this);
			this._onTouchMove = this._onTouchMove.bind(this);
			this._onTouchEnd = this._onTouchEnd.bind(this);
		}

		// Adding listeners for check mobile touch: "start","move","end" and listeners for check keyboard: "click" and "keydown"
		componentDidMount() {
			document.addEventListener("touchstart", this._onTouchStart, false);
			document.addEventListener("touchmove", this._onTouchMove, false);
			document.addEventListener("touchend", this._onTouchEnd, false);
			document.addEventListener("click", this._handleClick, false);
			document.addEventListener("keydown", this._handleKey, false);
		}

		// Remove listeners for check mobile touch: "start","move","end" and listeners for check keyboard: "click" and "keydown"
		componentWillUnmount() {
			document.removeEventListener("touchstart", this._onTouchStart);
			document.removeEventListener("touchmove", this._onTouchMove);
			document.removeEventListener("touchend", this._onTouchEnd);
			document.removeEventListener("click", this._handleClick);
			document.removeEventListener("keydown", this._handleKey);
		}

		/**
		 * Send touch "start" event to middleware(saga+redux) processing
		 * @param e - touch start event {@link SyntheticEvent}
		 * @private
		 */
		_onTouchStart(e) {
			const { touchStartMenu } = this.props;
			const touch = e.changedTouches[0];
			touchStartMenu(touch.clientX);
		}

		/**
		 * Send touch "move" event to middleware(saga+redux) processing
		 * @param e - touch start event {@link SyntheticEvent}
		 * @private
		 */
		_onTouchMove(e) {
			if (e.changedTouches && e.changedTouches.length) {
				const { touchMoveMenu } = this.props;
				touchMoveMenu(e.changedTouches[0].clientX);
			}
		}

		/**
		 * Send touch "end" event to middleware(saga+redux) processing
		 * @private
		 */
		_onTouchEnd() {
			const { touchEndMenu, swipe, minSwipeDistance, side_names } = this.props;
			const { startX, endX, swiping } = swipe;
			const { left, right } = side_names;
			touchEndMenu({
				startX,
				endX,
				swiping,
				minSwipeDistance,
				left,
				right,
			});
		}

		/**
		 * Send key down event to middleware(saga+redux) processing
		 * @param e - Key down event {@link SyntheticEvent}
		 * @private
		 */
		_handleKey(e) {
			const { allKeyMenu } = this.props;
			allKeyMenu(e.key);
		}

		/**
		 * Send click event to middleware(saga+redux) processing
		 * @param e - Click event {@link SyntheticEvent}
		 * @private
		 */
		_handleClick(e) {
			const { defaultParams, excludeClose, nav_side, allClickMenu } = this.props;

			const path = e.path || (e.composedPath && e.composedPath()) || [e.target];

			const { settings, chats } = defaultParams.constants.navigation.buttonId;

			allClickMenu({
				excludeClose,
				path: path[0],
				buttonIds: [settings, chats],
				nav_side: {
					[settings]: { show: nav_side[settings].show },
					[chats]: { show: nav_side[chats].show },
				},
			});
		}

		/**
		 * Creating "setting" and "chats" components use side name for render it in specific side
		 * @param side - Side name "left" or "right"
		 * @returns {JSX.Element}
		 * @private
		 */
		_loadSideMenu(side) {
			const { side_names, mode, nav_side, strings } = this.props;

			const menuName = side_names[side];
			return (
				<Side
					values={{
						side,
						mode,
						body: this[`_initSide_${menuName}`](side),
						show: nav_side[menuName].show,
						buttonId: nav_side[menuName].buttonId,
						strings: {
							up: strings.up,
							down: strings.down,
							side: strings[menuName],
						},
						headerName: strings[nav_side[menuName].headerName],
					}}
				/>
			);
		}

		/**
		 * Creating "Settings menu" component for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_initSide_settings() {
			const { store, strings, user } = this.props;
			return (
				<Provider store={store.app}>
					<SideSettings
						{...{
							store,
							user: { username: user.username },
							settings_titles: strings.settings_titles,
						}}
					/>
				</Provider>
			);
		}

		/**
		 * Creating "Chats menu" component for render it
		 * @returns {JSX.Element}
		 * @private
		 */
		_initSide_chats(side) {
			const { store, user, lastChat, mode } = this.props;
			return (
				<Provider store={store.side.chats}>
					<SideUsers
						{...{
							lastChat,
							user,
							side,
							mode,
							store,
						}}
					/>
				</Provider>
			);
		}

		render() {
			return (
				<>
					{this._loadSideMenu("right")}
					{this._loadSideMenu("left")}
				</>
			);
		}
	}
);
