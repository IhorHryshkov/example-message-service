/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T00:50
 */
//--------index.js.js--------
import '../../../assets/css/components/common/InputMessage.css'

import React, {Component}         from 'react';
import {Button, Form, InputGroup} from 'react-bootstrap';
import {Formik}                   from 'formik';

import {load as loadSvg} from '../../../assets/svg';

class InputMessage extends Component {
	render() {
		const {strings, exceptionMessage, schema} = this.props;

		return (<Formik
				validationSchema={schema}
				onSubmit={(values) => console.log(values)}
				initialValues={{
					message: ''
				}}
			>{({
				handleSubmit,
				handleChange,
				values,
				touched,
				errors
			}) => (
				<Form noValidate onSubmit={handleSubmit} className={"message-form"}>
					<div className={"message-form-row"}>
						<InputGroup className={"textarea-container"}>
							<Form.Control
								as="textarea"
								className={"textarea-message"}
								name={'message'}
								value={values.message}
								onChange={handleChange}
								// onBlur={handleBlur}
								placeholder={strings.message.placeholder}
								isValid={touched.message && !errors.message}
								isInvalid={!!errors.message || exceptionMessage}
							/>
							<Form.Control.Feedback type="invalid" className={"textarea-message-error"}>
								{errors.message ? errors.message : exceptionMessage ? exceptionMessage : ''}
							</Form.Control.Feedback>
						</InputGroup>
						<div className={"btn-circle-container"}>
							<Button variant="primary" type='submit' className={"btn-circle"}>
								{loadSvg(
									"send",
									"btn-circle-img",
									""
								)}
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
