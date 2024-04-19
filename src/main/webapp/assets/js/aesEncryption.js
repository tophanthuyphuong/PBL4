/*const encrypteFunction = function(text, key, iv) {
	let encrypted = CryptoJS.AES.encrypt(text, key, { iv: iv });
	return encrypted.toString(); // Chuyển kết quả mã hóa sang dạng chuỗi để lưu trữ hoặc truyền đi
}*/
/*const decrypteFunction = function(text, key, iv) {
	let decrypted = CryptoJS.AES.decrypt(text, key, { iv: iv });
	return decrypted.toString(CryptoJS.enc.Utf8); // Chuyển kết quả giải mã về dạng UTF-8 để lấy văn bản gốc
}*/
const encrypteFunction = function(textToEncrypt, key, iv) {
    const keyBytes = CryptoJS.enc.Base64.parse(key);
    const ivBytes = CryptoJS.enc.Base64.parse(iv);

    const encrypted = CryptoJS.AES.encrypt(textToEncrypt, keyBytes, {
        iv: ivBytes,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return encrypted.toString();
}

const decrypteFunction = function(encryptedText, key, iv) {
    const keyBytes = CryptoJS.enc.Base64.parse(key);
    const ivBytes = CryptoJS.enc.Base64.parse(iv);
    const encryptedData = CryptoJS.enc.Base64.parse(encryptedText);

    const decrypted = CryptoJS.AES.decrypt(
        { ciphertext: encryptedData },
        keyBytes,
        { iv: ivBytes, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 }
    );

    return decrypted.toString(CryptoJS.enc.Utf8);
}

function encryptDefault(text) {
	var defaultKey = localStorage.getItem('defaultKey');
    var defaultIV = localStorage.getItem('defaultIV');
	if (defaultKey && defaultIV) {
		text = encrypteFunction(text, defaultKey, defaultIV);
	}
	return text;
}

function encryptPrivate(text) {
	var privateKey = localStorage.getItem('private_key');
    var privateIV = localStorage.getItem('private_iv');
	if (privateKey && privateIV) {
		text = encrypteFunction(text, privateKey, privateIV);
	}
	return text;
}
function decryptPrivate(text) {
	var privateKey = localStorage.getItem('private_key');
    var privateIV = localStorage.getItem('private_iv');
	if (privateKey && privateIV) {
		text = decrypteFunction(text, privateKey, privateIV);
	}
	return text;
}

