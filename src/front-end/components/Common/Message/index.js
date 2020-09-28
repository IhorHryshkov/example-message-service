/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T21:33
 */
//--------index.js--------
import '../../../assets/css/components/chat/Message.css'
import React, {Component} from 'react';
import {Card}             from 'react-bootstrap';

class Message extends Component {
	render() {
		const {message} = this.props;
		return (
			<Card key={message.id}
				  className={`message ${message.own ? 'left' : 'right'}`}>
				<Card.Body>
					{message.body}
				</Card.Body>
			</Card>
		);
	}

}

export default Message;