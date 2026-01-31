# Grace Application

<img src="https://github.com/ViksaaSkool/Grace/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="128" height="128"/>

So what is this about? It's simple - a mobile application that lets you bless your meals before you
eat. You can upload an image from your gallery,
or take a photo of a meal and let his Grace bless it. His Grace uses on-device ML Kit image labeling
to check if the photo is of a meal, and if it is,
he does his magic.

<img src="https://github.com/ViksaaSkool/Grace/blob/master/art/mobile.png" width="190" height="360"/>

For more visit the [offical page](https://blessameal.com/).
Needles to say - it's a joke. 

# Kotlin + Compose Refactor Summary

- Fully migrated Java → Kotlin and removed legacy MVP/Dagger/EventBus stack.
- Rebuilt UI with Jetpack Compose and a single-activity + Navigation setup.
- Adopted MVVM + StateFlow with coroutines for background work.
- Added Hilt for dependency injection.
- Preserved SharedPreferences for T&C acceptance state.
- Kept on-device ML Kit image labeling for meal detection.

Refactor artifacts: [art/refactored_prompts](art/refactored_prompts)

# Here are the screenshots
1. Upload photo
<img src="https://github.com/ViksaaSkool/Grace/blob/master/art/m1.png" width="190" height="280"/>

2. Let his Grace inspect the photo
<img src="https://github.com/ViksaaSkool/Grace/blob/master/art/b1.png" width="190" height="280"/>

3. If it's photo of a meal - let his Grace know if you want the meal to be blessed
<img src="https://github.com/ViksaaSkool/Grace/blob/master/art/b3.png" width="190" height="280"/>

4. Share the blessed meal with your firends 
<img src="https://github.com/ViksaaSkool/Grace/blob/master/art/m2.png" width="190" height="220"/>

# Download the app to please the Gods
You can download and test the app by clicking [here](https://github.com/ViksaaSkool/Grace/raw/master/apk/app-release.apk).



License
--------

    The MIT License (MIT)

    Copyright (c) 2018 Viktor Arsovski
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.


# Developed By
Viktor Arsovski
</br>
<a href="https://mk.linkedin.com/in/varsovski">
  <img alt="Add me to Linkedin" src="http://is.gd/u42ILV" width="96" height="96"/>
</a>
