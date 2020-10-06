/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-26T14:58
 */
//--------index.js--------

import '../../../../../assets/css/components/navigation/SideUsers.css'
import React, {Component}    from 'react';
import {ButtonGroup, Button} from 'react-bootstrap';
import {connect}             from "react-redux";

import {selectUser, allUsers} from '../../../../../mq/actions/navigation/Common/Side/Users';
import LoadData               from '../../../../Common/Spinner';

const mapDispatchToProps = (dispatch) => {
	return {
		selectUser: payload => dispatch(selectUser(payload)),
		allUsers  : payload => dispatch(allUsers(payload))
	};
};
const mapStateToProps    = (state) => {
	return state;
};

class Index extends Component {

	componentDidMount() {
		this.props.allUsers();
	}

	_renderInProgress() {
		const {strings, mode} = this.props;
		return <LoadData {...{
			mode,
			text      : strings.load,
			size      : "xl",
			classNames: "absolute"
		}}/>;
	}

	_renderUsers() {
		const {selectUser, users, mode} = this.props;
		return <ButtonGroup id={"side-users-chat"} vertical className={"users-button-group"}>
			{
				users && users.length > 0 ? users.map(user =>
					<Button
						block
						id={"side-users-chat"}
						key={user.id}
						variant={`outline-${mode}`}
						active={user.isChat}
						onClick={() => selectUser(user.id)}>
						{user.username}
					</Button>
				) : ''
			}
		</ButtonGroup>
	}

	render() {
		const {progress} = this.props;
		return progress ? this._renderInProgress() : this._renderUsers();
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(
	Index);
