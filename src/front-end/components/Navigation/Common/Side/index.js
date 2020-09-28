/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:53
 */
//--------Index.js--------

import '../../../../assets/css/components/navigation/Side.css';
import React, {Component} from 'react';
import {Card, Container}  from 'react-bootstrap';
import {load as loadSvg}  from '../../../../assets/svg';
import CustomScroll       from '../../../Common/Scrolls/Base';

class Index extends Component {
	render() {
		const {side, show, mode, buttonId, headerName, body} = this.props.values;
		return (
			<Card
				className={`sidenav-main ${side} ${show} ${mode}`}
				id="side-menu">
				<div className={`menu-image-component ${side} ${mode}`}
					 id={buttonId}>
					{loadSvg(
						buttonId,
						`menu-image ${mode}`,
						buttonId
					)}
				</div>
				<div
					className={`sidenav sidenav-header-footer sidenav-header ${side} ${mode}`}
					id="side-menu">
					{loadSvg(
						'up',
						`menu-image-header-footer ${mode}`,
						``
					)}
				</div>
				<div id="side-menu-body"
					 className={`side-menu-user ${mode}`}>{headerName}</div>
				<div className={`sidenav sidenav-inner ${side} ${mode}`}
					 id="side-menu-body">
					<CustomScroll removeTracksWhenNotUsed mode={mode === 'light' ? 'dark' : 'light'}>
						<Container style={{
							width       : "100%",
							paddingLeft : "2px",
							paddingRight: "2px",
							direction   : "ltr"
						}}>
							{body}
						</Container>
					</CustomScroll>
				</div>
				<div
					className={`sidenav sidenav-header-footer sidenav-footer ${side} ${mode}`}
					id="side-menu">
					{loadSvg(
						'down',
						`menu-image-header-footer ${mode}`,
						``
					)}
				</div>
			</Card>
		);
	}
}

export default Index;
