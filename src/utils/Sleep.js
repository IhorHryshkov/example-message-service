/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-03T12:45
 */
// --------Sleep.js--------

/**
 * Utils for create sleep process on specific time in milliseconds
 */
export default ms => new Promise(resolve => setTimeout(resolve, ms));
