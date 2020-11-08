/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-15T00:37
 */
import Scrollbar from "react-scrollbars-custom";

// --------index.jsx--------

export default class RootScroll extends Scrollbar {
	// Execute scroll to bottom if values has execScrollToBottom
	componentDidUpdate(prevProps, prevState) {
		super.componentDidUpdate(prevProps, prevState);
		if (this.props.values && this.props.values.execScrollToBottom) {
			this.scrollToBottom();
		}
	}
}
