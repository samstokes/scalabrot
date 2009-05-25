build:
	find src -name '*.scala' | xargs scalac -d bin

demo: build
	scala -cp bin uk.co.samstokes.scalabrot.app 800 600 escapeTime demo.png

desktop: build
	scala -cp bin uk.co.samstokes.scalabrot.app 1920 1200 escapeTime desktop.png

look: demo
	eog demo.png
