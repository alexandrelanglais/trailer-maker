# Trailer Maker
**What is it?**

It's a small program that generates trailers from a video file.

**How does it work?**

It cuts parts from within the video of a specified cut length at various timestamps. Then, the cut parts are glued together.

**How can I generate a trailer?**
 1. Clone the project to your local repository
 2. run "sbt assembly"
 3. Run the following command: 
 >java -jar trailer-maker.jar -f <input-file-path> -d 15000 -l 1000 -s 3000 --preserve -o <output-path>

**What are the available options?**
 * -f: input file [required]
 * -d: duration in milliseconds of the generated trailer [required]
 * -l: cut length in millisecond of each parts of the trailer [required]
 * -o: output path
 * -s: start time in the original video in milliseconds
 * --preserve: preserve the original file name
 * --prepend-length: prepend the duration to the name of the generated trailer

**Are there required dependencies?**

Yes, you will need java and ffmpeg installed on your environment.

**Is there another way?**

Yes, you can go to [trailermaker.io](http://trailermaker.io) to generate your trailer

**Any way to reach out to you?**

You can message me on Github or go to  [/r/trailermaker](https://www.reddit.com/r/trailermaker/) on reddit.