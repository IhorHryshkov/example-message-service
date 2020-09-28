/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T23:39
 */
//--------index.js--------

import React, {Component} from 'react';
import {Button, Card, Form, InputGroup, Spinner} from 'react-bootstrap';
import {addUser, addUserSuccess} from '../../mq/actions/user';
import {connect} from 'react-redux';
import {Formik} from 'formik';

const mapDispatchToProps = (dispatch) => {
	return {
		addUser: obj => dispatch(addUser(obj))
	};
};

const mapStateToProps = (state) => {
	return state;
};

class Login extends Component {
	componentDidUpdate(prevProps, prevState, snapshot) {
		const {progress, store, user} = this.props;
		if (!progress && progress !== prevProps.progress) {
			store.app.dispatch(addUserSuccess(user));
		}
	}

	render() {
		// const {mode}            = this.props.values;
		const {strings, schema, progress, exception} = this.props;
		return (
			<Card className={"text-center"}>
				<Card.Header as="h5">{strings.login}</Card.Header>
				<Card.Body>
					<fieldset disabled={progress}>
						<Formik
							validationSchema={schema}
							onSubmit={(values) => this.props.addUser(values)}
							initialValues={{
								username: ''
							}}
						>{({
							handleSubmit,
							handleChange,
							handleBlur,
							values,
							touched,
							errors
						}) => (
							<Form noValidate onSubmit={handleSubmit}>
								<Form.Group controlId="formGridUsername">
									<InputGroup>
										<InputGroup.Prepend>
											<InputGroup.Text id="inputGroupPrepend">@</InputGroup.Text>
										</InputGroup.Prepend>
										<Form.Control
											name={'username'}
											value={values.username}
											onChange={handleChange}
											onBlur={handleBlur}
											placeholder={strings.username.placeholder}
											aria-describedby="inputGroupPrepend"
											isValid={touched.username && !errors.username}
											isInvalid={!!errors.username || exception}
										/>
										<Form.Control.Feedback type="invalid">
											{errors.username ? errors.username : exception ? exception : ''}
										</Form.Control.Feedback>
									</InputGroup>
								</Form.Group>
								<Button variant="primary" block type='submit'>
									{progress ? <>
											<Spinner
												as="span"
												animation="border"
												size="sm"
												role="status"
												aria-hidden="true"
											/>
											<span className="sr-only">
												{strings.login}...
											</span></> :
										strings.go}
								</Button>
							</Form>
						)}
						</Formik>
					</fieldset>
				</Card.Body>
			</Card>
		);
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	Login);
