# Where Am I?

> Personal projects are an opportunity to experiment outside the constraints of production work. This one is a geography guessing game, where players are shown a photo and players need to identify where in the world it was taken.

### Key Technologies

- **Maps SDK** - Core to gameplay; players place a pin on the map to make their guess, with scoring based on proximity to the actual location.
- **Jetpack Compose** - UI built entirely in Compose, following modern Android patterns such as MVI for a unidirectional from of data.
- **Switchable backend datasets** - Location data sourced from multiple APIs, kept loosely coupled so datasets can be swapped or extended independently.
- **Room** - Local persistence for storing high scores.

### Architecture

Clean Architecture, trying out a "by feature and by layer" structure, where each feature has separate module for the UI layer, and domain driven modularization for the domain and data layers. The drawback is more module overhead (mental complexity) over pure layer-based or feature-based modularization, but allows combining of functionality pragmatically in the features themselves, ressults in a very rational dependency tree.  
