/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T00:50
 */
//--------index.js--------
import '../../../../assets/css/components/common/CustomScroll.css';
import React, {Component} from 'react';
import Scrollbar          from 'react-scrollbars-custom';

export default class CustomScroll extends Component {
	render() {
		const {classes, mode} = this.props;
		return (
			<Scrollbar
				removeTracksWhenNotUsed
				thumbYProps={{className: `${!classes ? 'scroll-thumb' : classes} ${mode}`}}
				trackYProps={{className: `${!classes ? 'scroll-track' : classes} ${mode}`}}
				{...this.props}/>
		);
	}
}
