/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-24T21:33
 */
// --------index.jsx--------
import "../../../../assets/css/components/chat/Message.css";
import React, { Component } from "react";
import { Card, Row } from "react-bootstrap";

export default class Message extends Component {
	render() {
		const { message, mode, user_id } = this.props;

		const side = message.user_id === user_id ? "right" : "left";
		return (
			<Row className={`message-row ${side}`}>
				<Card key={message.id} className={`message ${side} ${mode}`}>
					<Card.Body className="message-body">{message.body}</Card.Body>
				</Card>
			</Row>
		);
	}
}
