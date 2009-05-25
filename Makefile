build:
	find src -name '*.scala' | xargs scalac -d bin

demo: build
	scala -cp bin uk.co.samstokes.scalabrot.app 800 600 demo.png

look: demo
	eog demo.png
