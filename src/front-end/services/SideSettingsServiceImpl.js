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
		obj.data.timestamp = new Date().getTime();
		return this._preferencesDao.add(obj);
	}

	async all(obj) {
		const result         = await this._preferencesDao.getByKey(obj);
		const preferencesTTL = new Date().getTime() - result.timestamp;
		const {maxTTL}       = this._defaultParams.constants.global.preferences;
		if (preferencesTTL > maxTTL) {
			result.user.id = "";
		}
		return result;
	}
}

export default SideSettingsServiceImpl;
