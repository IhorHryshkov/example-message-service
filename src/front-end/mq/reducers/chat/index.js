/**
 * @project example-message-service
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-25T08:09
 */
//--------index.js--------

import * as Yup from "yup";

import {constants}      from '../../../../config/front-end/constants.json';
import LocalizedStrings from "react-localization";
import localization     from '../../../../config/front-end/components/chat/localization.json';

const {ERROR}   = constants.global.actions;
const {message} = constants.chat.validation;
const strings   = new LocalizedStrings(localization);

const initialState = {
	strings,
	schema  : Yup.object({
		message: Yup.string()
			.min(
				message.min,
				strings.message.min
			)
			.required(strings.message.required)
	}),
	messages: [
		{
			id  : "f0c9f0a2-ac24-4fda-bff8-39f1a6baa153",
			own : true,
			body: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla hendrerit gravida tempor. Ut venenatis convallis augue laoreet malesuada. Proin non gravida est, ut porta libero. Nullam ac pretium ipsum, ac convallis eros. Maecenas imperdiet, mi sit amet cursus pellentesque, augue mauris ullamcorper nunc, egestas aliquet ex magna in tellus. Etiam sit amet nisl ultrices, bibendum ex sit amet, maximus libero. Nunc in sem molestie erat facilisis lobortis vel ac lectus. Mauris porttitor nunc posuere bibendum congue. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Proin sodales ullamcorper tincidunt.\n" +
				"\n" +
				"Nam at laoreet odio. Nunc at urna iaculis, porta enim vel, elementum nisl. Duis nunc tellus, volutpat quis porta sed, venenatis non lectus. Nunc neque est, vestibulum eu convallis imperdiet, pharetra vitae nibh. Mauris vel ullamcorper metus. Phasellus eu leo congue, congue sapien in, varius mi. Donec sollicitudin condimentum nisi, eu luctus orci fermentum sit amet. Cras vehicula, purus id euismod mattis, quam nunc malesuada purus, quis dictum nunc nulla blandit sapien. Donec fringilla ipsum id lacinia mattis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Quisque accumsan risus massa, at commodo mauris facilisis eu. Donec quis ullamcorper metus. Sed tempus nisi et nisi fermentum, sed ullamcorper nunc accumsan. Mauris tempor blandit felis in porttitor.\n" +
				"\n" +
				"Nullam urna lectus, iaculis at nulla ut, semper euismod lorem. Nulla facilisi. Pellentesque tempus sodales quam, posuere dictum ex. Vivamus eleifend hendrerit varius. Quisque sed hendrerit nibh, in interdum augue. In ac felis lacus. Nam eu dapibus nulla, nec malesuada ligula. Duis hendrerit non tortor et luctus. Morbi iaculis, magna nec volutpat tincidunt, dui velit scelerisque dolor, sed sollicitudin sem lacus in massa. Integer efficitur efficitur massa, non efficitur arcu fringilla auctor.\n" +
				"\n" +
				"Aenean accumsan tellus vitae odio laoreet, eget bibendum ipsum suscipit. Duis ac tellus at nunc vehicula rutrum. Proin ullamcorper et urna in eleifend. Sed consectetur finibus mauris, vitae tincidunt sapien consequat at. Mauris sodales magna ut magna ultrices commodo. Suspendisse mollis felis risus, vitae hendrerit nisl consectetur ut. Etiam orci odio, porta sit amet porta quis, rhoncus sit amet urna. Donec euismod lorem at tellus molestie suscipit. Suspendisse fringilla, dui ac mollis lacinia, quam orci aliquam lorem, id malesuada lacus augue sed felis. Quisque in convallis quam. Nulla ut semper odio, vitae semper nisi. Mauris accumsan sollicitudin erat, quis finibus velit dapibus vitae.\n" +
				"\n" +
				"In ut felis et tellus tincidunt pulvinar eget ac ex. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nam eleifend purus eu est tincidunt placerat. Curabitur pharetra tortor vel nisl mattis, at malesuada ante lacinia. Donec mattis justo quis sagittis sagittis. Donec at porttitor est. Morbi nisi dui, egestas vitae interdum ut, ornare a ipsum. Aenean at lorem id felis egestas lacinia in vel enim. Ut non finibus tortor, a luctus augue. Quisque tempus metus sit amet lacus rhoncus, sit amet egestas dui porttitor. Maecenas at mauris erat."
		},
		{
			id  : "f0c9f0a2-ac24-4fda-bff8-39f1a6baa777",
			own : false,
			body: "Ooooohohohohohohohoho"
		},
		{
			id  : "f0c9f0a2-ac24-4fda-bff8-39f1a6baa156",
			own : true,
			body: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla hendrerit gravida tempor. Ut venenatis convallis augue laoreet malesuada. Proin non gravida est, ut porta libero. Nullam ac pretium ipsum, ac convallis eros. Maecenas imperdiet, mi sit amet cursus pellentesque, augue mauris ullamcorper nunc, egestas aliquet ex magna in tellus. Etiam sit amet nisl ultrices, bibendum ex sit amet, maximus libero. Nunc in sem molestie erat facilisis lobortis vel ac lectus. Mauris porttitor nunc posuere bibendum congue. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Proin sodales ullamcorper tincidunt.\n" +
				"\n" +
				"Nam at laoreet odio. Nunc at urna iaculis, porta enim vel, elementum nisl. Duis nunc tellus, volutpat quis porta sed, venenatis non lectus. Nunc neque est, vestibulum eu convallis imperdiet, pharetra vitae nibh. Mauris vel ullamcorper metus. Phasellus eu leo congue, congue sapien in, varius mi. Donec sollicitudin condimentum nisi, eu luctus orci fermentum sit amet. Cras vehicula, purus id euismod mattis, quam nunc malesuada purus, quis dictum nunc nulla blandit sapien. Donec fringilla ipsum id lacinia mattis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Quisque accumsan risus massa, at commodo mauris facilisis eu. Donec quis ullamcorper metus. Sed tempus nisi et nisi fermentum, sed ullamcorper nunc accumsan. Mauris tempor blandit felis in porttitor.\n" +
				"\n" +
				"Nullam urna lectus, iaculis at nulla ut, semper euismod lorem. Nulla facilisi. Pellentesque tempus sodales quam, posuere dictum ex. Vivamus eleifend hendrerit varius. Quisque sed hendrerit nibh, in interdum augue. In ac felis lacus. Nam eu dapibus nulla, nec malesuada ligula. Duis hendrerit non tortor et luctus. Morbi iaculis, magna nec volutpat tincidunt, dui velit scelerisque dolor, sed sollicitudin sem lacus in massa. Integer efficitur efficitur massa, non efficitur arcu fringilla auctor.\n" +
				"\n" +
				"Aenean accumsan tellus vitae odio laoreet, eget bibendum ipsum suscipit. Duis ac tellus at nunc vehicula rutrum. Proin ullamcorper et urna in eleifend. Sed consectetur finibus mauris, vitae tincidunt sapien consequat at. Mauris sodales magna ut magna ultrices commodo. Suspendisse mollis felis risus, vitae hendrerit nisl consectetur ut. Etiam orci odio, porta sit amet porta quis, rhoncus sit amet urna. Donec euismod lorem at tellus molestie suscipit. Suspendisse fringilla, dui ac mollis lacinia, quam orci aliquam lorem, id malesuada lacus augue sed felis. Quisque in convallis quam. Nulla ut semper odio, vitae semper nisi. Mauris accumsan sollicitudin erat, quis finibus velit dapibus vitae.\n" +
				"\n" +
				"In ut felis et tellus tincidunt pulvinar eget ac ex. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nam eleifend purus eu est tincidunt placerat. Curabitur pharetra tortor vel nisl mattis, at malesuada ante lacinia. Donec mattis justo quis sagittis sagittis. Donec at porttitor est. Morbi nisi dui, egestas vitae interdum ut, ornare a ipsum. Aenean at lorem id felis egestas lacinia in vel enim. Ut non finibus tortor, a luctus augue. Quisque tempus metus sit amet lacus rhoncus, sit amet egestas dui porttitor. Maecenas at mauris erat."
		},
		{
			id  : "f0c9f0a2-ac24-4fda-bff8-39f1a6baa177",
			own : false,
			body: "The box-shadow property enables you to cast a drop shadow from the frame of almost any element. If a border-radius is specified on the element with a box shadow, the box shadow takes on the same rounded corners. The z-ordering of multiple box shadows is the same as multiple text shadows (the first specified shadow is on top).\n" +
				"\n" +
				"Box-shadow generator is an interactive tool allowing you to generate a box-shadow."
		}
	]
};

export default (state = initialState, action) => {
	switch (action.type) {
		case ERROR: {
			return {
				...state
			};
		}
		default:
			return state;
	}
};
