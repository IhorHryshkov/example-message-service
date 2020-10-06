/**
 *@project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:33
 */
//--------SideSettingsServiceImpl.js--------

import PreferencesDao from '../database/dao/local/PreferencesDaoImpl'
import RootService    from './iface/RootService';

class SideSettingsServiceImpl extends RootService {
	constructor({dbConfig, defaultParams}) {
		super({defaultParams});
		this._preferencesDao = new PreferencesDao(
			dbConfig,
			defaultParams
		);
	}

	add(obj) {
		return this._preferencesDao.add(obj);
	}

	async all(obj) {
		let result     = await this._preferencesDao.getByKey(obj);
		result         = result ? result : {
			user: {timestamp: 0}
		};
		const calcTTL  = new Date().getTime() - result.user.timestamp;
		const {maxTTL} = this._defaultParams.constants.global.preferences;
		if (calcTTL > maxTTL) {
			result.user.endTTL = true;
		}
		return result;
	}
}

export default SideSettingsServiceImpl;
