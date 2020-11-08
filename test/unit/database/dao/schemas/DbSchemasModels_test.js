/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-10-26T14:59
 */
//--------DbSchemasModels_test.js--------

import fs from 'fs';

describe(
	`DB schemas JSON test`,
	() => {
		it(
			`Check JSON schemas models`,
			() => {
				const pathConfig = '/src/database/dao/schemas';
				const walkSync   = dir => {
					const files = fs.readdirSync(dir);
					files.filter(file => {
						if (fs.statSync(dir + '/' + file)
						.isDirectory()) {
							walkSync(
								dir + '/' + file
							);
						} else {
							return (file.indexOf(".json") > 0);
						}
					})
					.forEach(file => {
						const pathForTest  = dir.substring(dir.indexOf(pathConfig) + pathConfig.length, dir.length);
						const actualJson   = require(`${dir}/${file}`);
						const expectedJson = require(`.${pathForTest}/${file}`);
						expect(actualJson).toEqual(expectedJson);
					});
				};

				walkSync(`${__dirname}/../../../../../src/database/dao/schemas`);
			}
		);
	}
);
