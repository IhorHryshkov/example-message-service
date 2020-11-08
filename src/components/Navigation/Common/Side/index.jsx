/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:53
 */
// --------Index.jsx--------

import "../../../../assets/css/components/navigation/Side.css";
import React, { Component } from "react";
import { Card, Container } from "react-bootstrap";
import loadSvg from "../../../../assets/svg";
import CustomScroll from "../../../Common/Scrolls/Custom";

/**
 * Component for rendering left and right menu components
 */
export default class Side extends Component {
	render() {
		const { values } = this.props;
		const { side, show, mode, buttonId, headerName, body } = values;
		return (
			<Card className={`sidenav-main ${side} ${show} ${mode}`} id="side-menu">
				<div className={`menu-image-component ${side} ${mode}`} id={buttonId}>
					{loadSvg(buttonId, `menu-image ${mode}`, buttonId)}
				</div>
				<div className={`sidenav sidenav-header-footer sidenav-header ${side} ${mode}`} id="side-menu">
					{loadSvg("up", `menu-image-header-footer ${mode}`, ``)}
				</div>
				<div id="side-menu-body" className={`side-menu-user ${mode}`}>
					{headerName}
				</div>
				<div className={`sidenav sidenav-inner ${side} ${mode}`} id="side-menu-body">
					<CustomScroll removeTracksWhenNotUsed mode={mode === "light" ? "dark" : "light"}>
						<Container className="sidenav-inner-scroll-container">{body}</Container>
					</CustomScroll>
				</div>
				<div className={`sidenav sidenav-header-footer sidenav-footer ${side} ${mode}`} id="side-menu">
					{loadSvg("down", `menu-image-header-footer ${mode}`, ``)}
				</div>
			</Card>
		);
	}
}
