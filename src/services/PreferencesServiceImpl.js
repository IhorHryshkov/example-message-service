/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:33
 */
// --------PreferencesServiceImpl.js--------
import PreferencesDao from "../database/dao/PreferencesDaoImpl";
import RootService from "./iface/RootService";

/**
 * Service for preferences processing
 */
export default class PreferencesServiceImpl extends RootService {
	constructor({ dbConfig, defaultParams }) {
		super({ defaultParams });
		this._preferencesDao = new PreferencesDao(dbConfig, defaultParams);
	}

	/**
	 * Add preferences data
	 * @param key - Key for preferences data
	 * @param data - Data of preferences
	 	{
			mode: string,
			darkLightChecked: boolean,
			leftRightChecked: boolean,
			nav_side:{
				left: string,
				right: string
			},
			lastChat: string,
			user:{
				id: string,
				username: string,
				timestamp: number
			}
		}
	 * @returns {Promise.resolve<{
			message: string,
			code   : string
		}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	add({ key, data }) {
		return this._preferencesDao.add({
			key,
			data,
		});
	}

	/**
	 * Get preferences and if preferences not found generation data for new preferences
	 * @param key - Key for get preferences data
	 * @returns {Promise.resolve<
	 	{
			mode: string,
			darkLightChecked: boolean,
			leftRightChecked: boolean,
			nav_side:{
				left: string,
				right: string
			},
			lastChat: string,
			user:{
				id: string,
				username: string,
				timestamp: number,
				endTTL: boolean
			}
	 	}>|
	 	Promise.resolve<{
			user:{
				timestamp: number,
				endTTL: boolean
			}
	 	}>|
		Promise.reject<{
			message: string,
			code   : string
		}>}
	 */
	async all({ key }) {
		let result = await this._preferencesDao.getByKey({ key });
		result = result || {
			user: { timestamp: 0 },
		};
		const calcTTL = Date.now() - result.user.timestamp;

		const { maxOnlineTTL } = this._defaultParams.constants.global.preferences;
		if (calcTTL > maxOnlineTTL) {
			result.user.endTTL = true;
		}
		return result;
	}
}
