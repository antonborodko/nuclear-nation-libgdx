# Nuclear Nation (prototype)

An abandoned experiment in learning game development — engines, frameworks and
build tooling, rather than a finished game. A turn-based post-apocalyptic
strategy prototype: capturing cities, population as a resource, city management
screens.

This was the earliest iteration (Spring 2020), built with **Scala on libGDX**
and Gradle. It later evolved into
[nuclear-nation](https://github.com/xfactor2000/nuclear-nation), which went
through Godot (C#, F#) and back to libGDX with Scala and mill.

Of the two, this approach was the one I preferred: **libGDX is a framework,
not an engine** — you write the game loop, rendering and state yourself, and
the library stays out of the way. Godot was quicker to get something on
screen, but less interesting to take apart.

**Status:** abandoned; kept for reference. Code quality reflects early
prototyping.

## Building (as it was left, 2020)

```bash
./gradlew desktop:run
```

Requires a JDK 8–11.

## Credits

- [Commodore 64 UI skin](https://ray3k.wordpress.com/commodore64-ui-skin-for-libgdx/)
  by Raymond "Raeleus" Buckley — [CC BY 4.0](http://creativecommons.org/licenses/by/4.0/)
- Font ["Lunchtime Doubly So"](https://zone38.net/font/) by codeman38

## License

Code is released under the [MIT License](LICENSE).
