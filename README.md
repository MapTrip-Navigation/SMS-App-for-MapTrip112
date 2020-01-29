# SMS/Tetra App For MapTrip112

SMS and TETRA MapTrip112 are two Android Applications, which are based on a common code-base. That means, that any of these apps can process any of the messages, which follows the format conventions, that will be discussed later. 
The difference between these two apps is related to the medium, which is used for listening to the new incoming messages.

## 1.2. SMS MapTrip112

SMS MapTrip112 is an app, which uses built-in mechanism from Android OS to listen to the incoming standard SMS. When new SMS comes, the app gets a notification from the Android OS and parses the SMS. If the SMS contains information in one of the known formats, the application comes to the foreground and suggests to start navigation.

## 1.3. TETRA MapTrip112

TETRA MapTrip112 is an app, which uses Garmin SDK and Garmin FMI cable to be able to listen to the TETRA network. Garmin SDK is a Low-level SDK, which means, it provides high flexibility, but requires some knowledge from the user about the infrastructure:
- We listen all the time in the background for the new coming messages (Write the corresponding code ourselves)
- Messages, which we receive over TETRA are encoded with HEX encoding.
For example: Hello, world! → 48656C6C6F2C20776F726C6421

If the Message contains information in one of the known formats, the application comes to the foreground and suggests to start navigation.

## 1.4. Supported Formats

### 1.4.1 TVPN

The message has the format of TVPN{LAT}E{LONG}. 
Important aspects:
- No decimal point in initial representation.
- Moreover, coordinates are initially encoded with Hexadecimal representation with 8 digits.
- After converting from Hexadimal, representation contains always 6 digits after the decimal point.

As optional parameters, SoSi and Free Text parameters are available:
- TVPN{LAT}E{LONG};SoSi - Sondersignal without free text;
- TVPN{LAT}E{LONG};Zimmerbrand - Free text without Sondersignal;
- TVPN{LAT}E{LONG};SoSi;Zimmerbrand - Both Sondresignal and Free text info.

Examples:
- TVPN03216774E00CC9C78
- TVPN03216774E00CC9C78;SoSi
- TVPN03216774E00CC9C78;Zimmerbrand
- TVPN03216774E00CC9C780;SoSi;Zimmerbrand

### 1.4.2 #K01

The message has the format of #K01;N{LAT}E{LONG}
Important aspects:
- No decimal point in initial representation.
- Representation contains always 5 digits after the decimal point.

As optional parameters, SoSi and Free Text parameters are available:
- #K01;N{LAT}E{LONG};SoSi - Sondersignal without free text;
- #K01;N{LAT}E{LONG};Zimmerbrand - Free text without Sondersignal;
- #K01;N{LAT}E{LONG};SoSi;Zimmerbrand - Both Sondresignal and Free text info.

Examples:
- #K01;N5252082E1340940
- #K01;N5252082E1340940;SoSi
- #K01;N5252082E1340940;Zimmerbrand
- #K01;N5252082E1340940;SoSi;Zimmerbrand

### 1.4.3 Default SMS Format:

The message has the format of {LAT}, {LONG}

As optional parameters, SoSi and Free Text paramters are available:
- {LAT}, {LONG}; SoSi
- {LAT}, {LONG}; Zimmerbrand
- {LAT}, {LONG}; SoSi; Zimmerbrand

Examples
- 51.2123544, 6.12548543
- 51.2123544, 6.12548543;SoSi
- 51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG
- 51.2123544, 6.12548543;SoSi;Zimmerbrand Musterstrasse 26 3:OG