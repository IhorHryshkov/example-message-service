/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-25T07:25
 */
//--------index.js--------
import '../../assets/css/components/chat/Chat.css'
import React, {Component}    from 'react';
import {Col, Container, Row} from 'react-bootstrap';
import CustomScroll          from '../Common/Scrolls/Base';
import Message               from '../Common/Message';
import InputMessage          from '../Common/InputMessage';
import {connect}             from 'react-redux';

const mapStateToProps = (state) => {
	return state;
};

class Chat extends Component {
	render() {
		const {mode} = this.props;
		return <Col className={"main-col"}>
			<Row className={"chat-row"}>
				<CustomScroll
					removeTracksWhenNotUsed
					{...{mode}}
				>
					<Container fluid>
						{this._loadMessages()}
					</Container>
				</CustomScroll>
			</Row>
			<Row className={"send-message-row"}>
				<InputMessage {...this.props}/>
			</Row>
		</Col>;
	}

	_loadMessages() {
		const {messages} = this.props;
		return messages && messages.length > 0 ? messages.map(message =>
			<Row key={message.id} className={`send-message-list ${message.own ? 'right' : 'left'}`}>
				<Message message={message}/>
			</Row>
		) : '';
	}
}

export default connect(
	mapStateToProps,
	null
)(
	Chat)