/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-05T07:37
 */
// --------index.jsx--------
import "../../../assets/css/components/common/Spinner.css";
import React, { Component } from "react";
import { Spinner } from "react-bootstrap";

/**
 * Custom spinner component
 */
export default class LoadData extends Component {
	render() {
		const { text, mode, classNames, size } = this.props;
		return (
			<div className={`root-spinner ${classNames}`}>
				<Spinner
					{...{
						size,
						variant: mode,
					}}
					as="span"
					animation="grow"
					role="status"
					aria-hidden="true"
				/>
				<span className="sr-only">
					{text}
					...
				</span>
			</div>
		);
	}
}