/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:17
 */
// --------StatusServiceImpl.js--------
import RootService from "./iface/RootService";
import StatusRequest from "../network/request/StatusRequestImpl";
import StatusDao from "../database/dao/StatusDaoImpl";

/**
 * Service for status processing
 */
export default class StatusServiceImpl extends RootService {
	/**
	 * Initialization of configuration data, network and DAO
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, dbConfig, defaultParams }) {
		super({ defaultParams });
		this._statusRequest = new StatusRequest({
			netConfig,
			defaultParams,
		});
		this._statusDao = new StatusDao(dbConfig, defaultParams);
	}

	/**
	 * Get all statuses from server and add to DB
	 * @param obj - Some query params like a name or ID {id: number, name: string}
	 * @returns {Promise<number>}
	 */
	async all(obj) {
		const { status, data } = await this._statusRequest.all(obj);
		if (status === 200) {
			await this._statusDao.add({
				data: data.data,
			});
		}
		return status;
	}
}
