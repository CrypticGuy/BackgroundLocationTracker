<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
<h3 align="center">Background Location Tracker</h3>

  <p align="center">
    A dynamic geofence location tracking app for android
    <br />
    <a href="https://github.com/crypticguy/BackgroundLocationTracker"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/crypticguy/BackgroundLocationTracker">View Demo</a>
    ·
    <a href="https://github.com/crypticguy/BackgroundLocationTracker/issues">Report Bug</a>
    ·
    <a href="https://github.com/crypticguy/BackgroundLocationTracker/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

<!--[![Product Name Screen Shot][product-screenshot]](https://example.com)-->

The Background Location Tracker is a powerful location tracking system that allows users to set custom geofences on a map, track their movements in the background, and receive notifications when they enter or exit the designated locations. This open-source project provides developers with a comprehensive solution for integrating background geofencing functionality into their mobile applications. For the demo of the above system, currently the app creates a notification to alert the user. Also, on going in the background, the app launches a foreground notification to display and update the realtime location.

The idea sprouted to me because I always forget to ping my mother on arrival of office, despite her remining me a million times. So, decided on making a system which would notify me when I have reached the office or any place, to just give her a ping. 
On discussion with a couple of friends, I realized this could have multitudes of scope. So, decided on uploading here for anyone to find and get motivation.

Do share with me any cool project that you create with reference. Please ignore the bad coding design. This app was created in a caffeine high state working a fulltime job. Much love :heart: 

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Technologies
[![AndroidStudio][android-studio]][android-url]
[![Android][android]][android-url]
[![Kotlin][kotlin]][kotlin-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Map SDK
[![MapMyIndia][mapmyindia]][mapmyindia-url]

<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

### Installation

1. Get the API key for MapMyIndia from https://www.mapmyindia.com/
2. Clone the repo
   ```sh
   git clone https://github.com/crypticguy/BackgroundLocationTracker.git
   ```
3. Open the project using android studio (install from https://developer.android.com/studio)
4. Create a ```secrets.xml``` file under ```app > src > main > res > values``` 
5. Enter the values for the following keys in ```secrets.xml```
   ```xml
   <string name="mapmyindia_rest_api_key">xxx</string>
   <string name="mapmyindia_atlas_client_id">xxx</string>
   <string name="mapmyindia_atlas_client_secret">xxx</string>
   ```
6. Create an emulator or use a physical device
7. Run the app

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES 
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>
-->


<!-- ROADMAP -->
## Roadmap

- [ ] Integrate the callbacks into Android System
- [ ] Send a notification to another device containing the app of arrival

See the [open issues](https://github.com/crypticguy/BackgroundLocationTracker/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Vasu Goel - [(@vgoel_)](https://twitter.com/vgoel_) - vasu18322@iiitd.ac.in

Project Link: [https://github.com/crypticguy/BackgroundLocationTracker](https://github.com/crypticguy/BackgroundLocationTracker)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS 
## Acknowledgments

* []()
* []()
* []()

<p align="right">(<a href="#readme-top">back to top</a>)</p>
-->


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/crypticguy/BackgroundLocationTracker.svg?style=for-the-badge
[contributors-url]: https://github.com/crypticguy/BackgroundLocationTracker/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/crypticguy/BackgroundLocationTracker.svg?style=for-the-badge
[forks-url]: https://github.com/crypticguy/BackgroundLocationTracker/network/members
[stars-shield]: https://img.shields.io/github/stars/crypticguy/BackgroundLocationTracker.svg?style=for-the-badge
[stars-url]: https://github.com/crypticguy/BackgroundLocationTracker/stargazers
[issues-shield]: https://img.shields.io/github/issues/crypticguy/BackgroundLocationTracker.svg?style=for-the-badge
[issues-url]: https://github.com/crypticguy/BackgroundLocationTracker/issues
[license-shield]: https://img.shields.io/github/license/crypticguy/BackgroundLocationTracker.svg?style=for-the-badge
[license-url]: https://github.com/crypticguy/BackgroundLocationTracker/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/vasu-goel-10751518b/
[product-screenshot]: images/screenshot.png
[kotlin]: https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white
[kotlin-url]: https://kotlinlang.org
[android-studio]: https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white
[android]: https://img.shields.io/badge/Android-11-blue
[mapmyindia]: https://cdn-mmi.b-cdn.net/images/logo-m.png
[mapmyindia-url]: https://www.mapmyindia.com/
[android-url]: https://developer.android.com/
<!--[Next.js]: https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vue.js]: https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D
[Vue-url]: https://vuejs.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Laravel.com]: https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white
[Laravel-url]: https://laravel.com
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com -->
