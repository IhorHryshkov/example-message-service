/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:50
 */
// --------index.jsx--------
import "../../../../assets/css/components/common/CustomScroll.css";
import React, { Component } from "react";
import RootScroll from "../Root";

/**
 * Custom scroll component
 */
export default class CustomScroll extends Component {
	render() {
		const { children, classes, mode, removeTracksWhenNotUsed, values } = this.props;
		return (
			<RootScroll
				{...{
					children,
					values,
					removeTracksWhenNotUsed,
				}}
				thumbYProps={{ className: `${!classes ? "scroll-thumb" : classes} ${mode}` }}
				trackYProps={{ className: `${!classes ? "scroll-track" : classes} ${mode}` }}
			/>
		);
	}
}
