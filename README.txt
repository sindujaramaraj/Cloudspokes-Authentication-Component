Clouspokes Authentication Component
-----------------------------------
This application is developed using Google Application Engine

To Test Access URL:  https://cloudspokes-sf-authentication.appspot.com/

To get a access token post request as below
url: https://cloudspokes-sf-authentication.appspot.com/authenticate
data: username=some_username&password=passwordsecuritytoken

If Salesforce username is john@test.cloudspokes.com then in the request send only john for username.

Response
--------
Server response has two parts
{
	status: "success"/"failure",
	type: "error_type", //only when status is failure
	response: "actual response"
}

type can be
VALIDATION ERROR or SERVER ERROR

Sample Response:

{
  "response": {
    "id": "https:\/\/login.salesforce.com\/id\/00DU0000000Hj9VMAS\/005U0000000coDJIAY",
    "issued_at": "1313600432399",
    "instance_url": "https:\/\/na12.salesforce.com",
    "access_token": "00DU0000000Hj9V!AQUAQIJDR.LRxZTDZ0BabGirf86V1thnU8QMgNiWrmm0azDzWtitfdqT6E2OY0lwBFiFpr7qxFJPjkqRTtCH287ac0YXXbe2",
    "signature": "PYS9pXa0k4GRXRB1QQ2XICEfY\/bK7E\/8ZdVsCX\/iSrc="
  },
  "status": "success"
}

Response with error

{
  "response": {
    "error": "invalid_grant",
    "error_description": "authentication failure - Invalid Password"
  },
  "status": "failure",
  "type": "SERVER ERROR"
}


Test Cases
-----------
Test case is available in TestAuthenticatioService under test folder

Source
----------
https://github.com/sindujaramaraj/Cloudspokes-Authentication-Component