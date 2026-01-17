

# Android
New version
```
git tag <semver>
git push origin <semver>
# Will get built by jitpack
```

main-SNAPSHOT
```
Nothing, just deploy and wait for jitpack.io to build it, then
./gradlew clean build
from your project
```

# iOS
Nothing, just commit, references directly from git provider


# npm
```
npm login # unless logged_in?
npm version major|minor|patch
npm publish
```