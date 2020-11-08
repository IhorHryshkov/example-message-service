/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-19T01:20
 */
// --------Converter.js--------

/**
 * Converter utils
 */
/**
 * Convert big numbers to string with symbol
 * @param numb - Number for convert
 * @returns {string|*}
 */
export default numb => {
	if (numb <= 999) {
		return numb;
	}
	if (numb > 999 && numb <= 999999) {
		return `${Math.floor(numb / 1000)}K+`;
	}
	if (numb > 999999 && numb <= 999999999) {
		return `${Math.floor(numb / 1000000)}M+`;
	}
	return `${Math.floor(numb / 1000000000)}B+`;
};
