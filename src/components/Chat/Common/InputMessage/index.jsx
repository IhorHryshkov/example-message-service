/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T00:50
 */
// --------index.jsx--------
import "../../../../assets/css/components/common/InputMessage.css";

import React, { Component } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";
import { Formik } from "formik";

import loadSvg from "../../../../assets/svg";

export default class InputMessage extends Component {
	constructor(props) {
		super(props);
		this._handleKey = this._handleKey.bind(this);
	}

	// Adding listener for check keyboard: "keyup"
	componentDidMount() {
		document.addEventListener("keyup", this._handleKey, false);
	}

	// Remove listeners for check keyboard: "keyup"
	componentWillUnmount() {
		document.removeEventListener("keyup", this._handleKey);
	}

	/**
	 * Processing for trim value and send submit if were up keys Shift + Enter in keyboard
	 * @param e - Key up event {@link SyntheticEvent}
	 * @private
	 */
	_handleKey = e => {
		const { target } = e;
		if (target.id === "send-message-textarea") {
			if (e.keyCode === 13) {
				if (e.shiftKey && target.value && target.value.length > 0) {
					const evt = document.createEvent("Event");
					evt.initEvent("submit", true, true);
					document.getElementById("send-message-form").dispatchEvent(evt);
				}
			}
		}
	};

	render() {
		const { strings, exceptionMessage, schema, submit } = this.props;

		return (
			<Formik
				validationSchema={schema}
				onSubmit={(values, { resetForm }) => {
					submit(values);
					resetForm({});
				}}
				initialValues={{
					message: "",
				}}
			>
				{({ handleSubmit, handleChange, values, touched, errors }) => (
					<Form id="send-message-form" noValidate onSubmit={handleSubmit} className="message-form">
						<div className="message-form-row">
							<InputGroup className="textarea-container">
								<Form.Control
									id="send-message-textarea"
									as="textarea"
									className="textarea-message"
									name="message"
									value={values.message}
									onChange={handleChange}
									// onBlur={handleBlur}
									placeholder={strings.message.placeholder}
									isValid={touched.message && !errors.message}
									isInvalid={!!errors.message || exceptionMessage}
								/>
								<Form.Control.Feedback type="invalid" className="textarea-message-error">
									{errors.message ? errors.message : exceptionMessage || ""}
								</Form.Control.Feedback>
							</InputGroup>
							<div className="btn-circle-container">
								<Button variant="primary" type="submit" className="btn-circle">
									{loadSvg("send", "btn-circle-img", "")}
								</Button>
							</div>
						</div>
					</Form>
				)}
			</Formik>
		);
	}
}
