/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T14:58
 */
//--------index.js--------

import '../../../../../assets/css/components/navigation/SideSettings.css'
import React, {Component}    from 'react';
import {ButtonGroup, Button} from 'react-bootstrap';
import {connect}             from "react-redux";

import {selectUser, loadUser} from '../../../../../mq/actions/navigation/Common/Side/Users';

const mapDispatchToProps = (dispatch) => {
	return {
		selectUser: payload => dispatch(selectUser(payload)),
		loadUser  : payload => dispatch(loadUser(payload))
	};
};
const mapStateToProps    = (state) => {
	return state;
};

class Index extends Component {
	render() {
		const {selectUser, users, mode} = this.props;
		return (
			<ButtonGroup id={"side-users-chat"} vertical style={{width: "100%"}}>
				{
					users && users.length > 0 ? users.map(user =>
						<Button
							block
							id={"side-users-chat"}
							key={user.id}
							variant={`outline-${mode}`}
							active={user.isChat}
							disabled={user.status.name !== "Online"}
							onClick={() => selectUser(user.id)}>
							{user.username}
						</Button>
					) : ''
				}

			</ButtonGroup>
		);
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	Index);
