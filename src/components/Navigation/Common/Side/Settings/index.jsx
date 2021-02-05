/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:53
 */
// --------index.jsx--------

import "../../../../../assets/css/components/navigation/SideSettings.css";
import React, { Component } from "react";
import { Badge, Card, Col, Row } from "react-bootstrap";
import { connect } from "react-redux";

import { allUserCounters, changeMode, changeSideMenu, initSocket } from "../../../../../mq/actions/navigation/Common/Side/Settings";
import LoadData from "../../../../Common/Spinner";

const mapDispatchToProps = dispatch => {
	return {
		allUserCounters: payload => dispatch(allUserCounters(payload)),
		changeMode: () => dispatch(changeMode()),
		changeSideMenu: () => dispatch(changeSideMenu()),
		initSocket: obj => dispatch(initSocket(obj)),
	};
};

const mapStateToProps = state => {
	return state;
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	class Settings extends Component {
		// Init server socket if not init and load all user counters
		componentDidMount() {
			const { store, user, isInitSocket, initSocket, allUserCounters } = this.props;
			if (!isInitSocket) {
				initSocket({
					store,
					username: user.username,
				});
			}
			allUserCounters(user.id);
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
						mode,
						text: strings.load,
						size: "xl",
					}}
				/>
			);
		}

		/**
		 * Rendering list counters or nothing in settings menu
		 * @returns {*|JSX.Element}
		 * @private
		 */
		_renderCounters() {
			const { counters, mode } = this.props;
			return counters && counters.length > 0 ? (
				counters.map(counter => (
					<Row key={counter.name} className="side-menu-body2-row">
						<Col className="side-menu-body2-col name">{counter.name}</Col>
						<Col className="side-menu-body2-col counts">
							<Badge pill variant={mode}>
								{counter.counts}
							</Badge>
						</Col>
					</Row>
				))
			) : (
				<></>
			);
		}

		render() {
			const { changeMode, changeSideMenu, settings_titles, mode, darkLightChecked, leftRightChecked, user, counterProgress } = this.props;
			return (
				<>
					<Card.Body id="side-menu-body" className={`side-menu-username ${mode}`}>
						{user.username}
					</Card.Body>
					<Card.Header id="side-menu-body" className={`side-menu-header ${mode}`}>
						{settings_titles.counters}
					</Card.Header>
					<Card.Body id="side-menu-body" className={`side-menu-body2 ${mode}`}>
						{counterProgress ? this._renderInProgress() : this._renderCounters()}
					</Card.Body>
					<Card.Header id="side-menu-body" className={`side-menu-header ${mode}`}>
						{settings_titles.mode}
					</Card.Header>
					<Card.Body id="side-menu-body" className={`side-menu-body ${mode}`}>
						<div id="side-menu-dark-light-check">
							{settings_titles.darkLight}
							<input
								type="checkbox"
								checked={darkLightChecked}
								className={`dark-light-switch ${mode}`}
								onChange={changeMode}
								id="side-menu-dark-light-check"
							/>
						</div>
					</Card.Body>
					<Card.Header id="side-menu-body" className={`side-menu-header ${mode}`}>
						{settings_titles.menuSide}
					</Card.Header>
					<Card.Body id="side-menu-body" className={`side-menu-body ${mode}`}>
						<div id="side-menu-dark-light-check">
							{settings_titles.leftRight}
							<input
								type="checkbox"
								checked={leftRightChecked}
								className={`custom-switch-without-color ${mode}`}
								id="side-menu-left-right"
								onChange={changeSideMenu}
							/>
						</div>
					</Card.Body>
				</>
			);
		}
	}
);