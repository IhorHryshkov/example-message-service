/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T00:50
 */
//--------index.js.js--------
import '../../../assets/css/components/common/InputMessage.css'

import React, {Component} from 'react';
import {Button, Form}     from 'react-bootstrap';
import {Formik}           from 'formik';

import {load as loadSvg} from '../../../assets/svg';

class InputMessage extends Component {
	render() {
		return (<Formik
				initialValues={{
					message: ''
				}}
			>{({
				handleSubmit,
				handleChange,
				handleBlur,
				values,
				touched,
				errors
			}) => (
				<Form noValidate onSubmit={handleSubmit} className={"message-form"}>
					<div className={"message-form-row"}>
						<div className={"textarea-container"}>
							<Form.Control as="textarea" className={"textarea-message"}/>
						</div>
						<div className={"btn-circle-container"}>
							<Button variant="primary" type='submit' className={"btn-circle"}>
								{loadSvg("send","btn-circle-img","")}
							</Button>
						</div>
					</div>
				</Form>
			)}
			</Formik>
		);
	}
}

export default InputMessage;
