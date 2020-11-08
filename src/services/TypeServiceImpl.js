/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-15T19:59
 */
// --------TypeServiceImpl.js--------
import RootService from "./iface/RootService";
import TypeDao from "../database/dao/TypeDaoImpl";
import TypeRequest from "../network/request/TypeRequestImpl";

/**
 * Service for types processing
 */
export default class TypeServiceImpl extends RootService {
	/**
	 * Initialization of configuration data, network and DAO
	 * @param netConfig - Network config object {@link NetworkConfigImpl}
	 * @param dbConfig - Database config object {@link DatabaseConfigImpl}
	 * @param defaultParams - Default params and constants
	 */
	constructor({ netConfig, dbConfig, defaultParams }) {
		super({ defaultParams });
		this._typeRequest = new TypeRequest({
			netConfig,
			defaultParams,
		});
		this._typeDao = new TypeDao(dbConfig, defaultParams);
	}

	/**
	 * Get all types from server and add to DB
	 * @param obj - Some query params like a name or ID {id: number, name: string}
	 * @returns {Promise<number>}
	 */
	async all(obj) {
		const { status, data } = await this._typeRequest.all(obj);
		if (status === 200) {
			await this._typeDao.add({
				data: data.data,
			});
		}
		return status;
	}
}
