/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:53
 */
//--------index.js--------

import '../../../../../assets/css/components/navigation/SideSettings.css'
import React, {Component}      from 'react';
import {Badge, Card, Col, Row} from 'react-bootstrap';
import {connect}               from "react-redux";

import {changeMode, changeSideMenu} from '../../../../../mq/actions/navigation/Common/Side/Settings';

const mapDispatchToProps = (dispatch) => {
	return {
		changeMode    : () => dispatch(changeMode()),
		changeSideMenu: () => dispatch(changeSideMenu())
	};
};

const mapStateToProps = (state) => {
	return state;
};

class Index extends Component {

	_loadCounters() {
		const {counters, mode} = this.props;
		return counters && counters.length > 0 ? counters.map(counter =>
			<Row key={counter.name} className={"side-menu-body2-row"}>
				<Col className={"side-menu-body2-col name"}>
					{counter.name}
				</Col>
				<Col className={"side-menu-body2-col counts"}>
					<Badge pill variant={mode}>
						{counter.counts}
					</Badge>
				</Col>
			</Row>
		) : '';
	}

	render() {
		const {changeMode, changeSideMenu, settings_titles, mode, darkLightChecked, leftRightChecked, user} = this.props;
		return (
			<>
				<Card.Body id="side-menu-body" className={`side-menu-username ${mode}`}>
					{user.username}
				</Card.Body>
				<Card.Header id="side-menu-body" className={`side-menu-header ${mode}`}>
					{settings_titles.counters}
				</Card.Header>
				<Card.Body id="side-menu-body" className={`side-menu-body2 ${mode}`}>
					{this._loadCounters()}
				</Card.Body>
				<Card.Header id="side-menu-body" className={`side-menu-header ${mode}`}>
					{settings_titles.mode}
				</Card.Header>
				<Card.Body id="side-menu-body" className={`side-menu-body ${mode}`}>
					<div id="side-menu-dark-light-check">
						{settings_titles.darkLight}
						<input type="checkbox" checked={darkLightChecked}
							   className={`dark-light-switch ${mode}`}
							   onChange={changeMode}
							   id="side-menu-dark-light-check"/>
					</div>
				</Card.Body>
				<Card.Header id="side-menu-body" className={`side-menu-header ${mode}`}>
					{settings_titles.menuSide}
				</Card.Header>
				<Card.Body id="side-menu-body" className={`side-menu-body ${mode}`}>
					<div id="side-menu-dark-light-check">
						{settings_titles.leftRight}
						<input type="checkbox" checked={leftRightChecked}
							   className={`custom-switch-without-color ${mode}`}
							   id="side-menu-left-right" onChange={changeSideMenu}/>
					</div>
				</Card.Body>
			</>
		);
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	Index);
