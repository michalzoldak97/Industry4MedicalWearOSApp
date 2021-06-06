<h1 align="center"><strong>Industry 4 Medical<strong /></h1>
<h2 align="center">Wear OS App ver.1</h2>

<h3>API communication examples:</h3>

<p>User Login <strong> POST </strong>request body:</p>
<p>{
    "email": "someMail@example.com",
    "password": "passwordText"
  }</p>
 <p>Expected response body: </p>
 <p>{
    "name": "someMail@example.com",
    "token": "123secretToken123"
  }</p>
 <br />
 <p>Sleep data header:</p>
 <p>{Authorization=Bearer 123secretToken123, Accept=application/json, Content-Type=application/json}</p>
 <p>Sleep data body (acceleration):</p>
 {"DataType":"ACC"},{"2021-06-03 18:51:47.895":"[0.0, 9.81, 0.0]"},{"2021-06-03 18:51:59.109":"[-6.10708, 7.67722, 0.0]"},...
 <p>Sleep data body (heart rate):</p>
 <p>{"DataType":"HR"},{"2021-06-03 18:52:14.694":"[130.3]"},{"2021-06-03 18:52:23.703":"[111.7]"},{"2021-06-03 18:52:23.903":"[90.4]"},{"2021-06-03 18:52:24.103":"[77.1]"},...</p>
