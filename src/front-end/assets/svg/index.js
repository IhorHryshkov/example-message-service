/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:24
 */
//--------index.js--------
import React                          from 'react';
import {ReactComponent as SendIcon}   from './actions/local/send.svg';
import {ReactComponent as UsersIcon}  from './navigation/users.svg';
import {ReactComponent as MenuIcon}   from './navigation/menu.svg';
import {ReactComponent as UpIcon}     from './navigation/up.svg';
import {ReactComponent as DownIcon}   from './navigation/down.svg';
import {ReactComponent as SearchIcon} from './navigation/search.svg';

export const load = (name, classNames, id) => {
	switch (name) {
		case 'users': {
			return <UsersIcon className={classNames} id={id}/>;
		}
		case 'settings': {
			return <MenuIcon className={classNames} id={id}/>;
		}
		case 'up': {
			return <UpIcon className={classNames} id={id}/>;
		}
		case 'down': {
			return <DownIcon className={classNames} id={id}/>;
		}
		case 'search': {
			return <SearchIcon className={classNames} id={id}/>;
		}
		case 'send': {
			return <SendIcon className={classNames} id={id}/>;
		}
		default:
			return '';
	}
}
