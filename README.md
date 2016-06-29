# LineMonitorBot
Enterprise productivity app for use in a plastics extrusion plant. 

[See how it works](https://play.google.com/store/apps/details?id=com.kovaciny.linemonitorbot) on the Play Store.

## Bug reporting
[See bug status]( https://bitbucket.org/Noumenon72/linemonitorbot/issues?status=new&status=open&sort=-priority) at bitbucket.org.

## Design notes
This app grew up from a very basic MIT App Inventor product to something I used all day at work. I also shared it with my coworkers, so UI hints, easy touch targets, and responsiveness were important.

The 19 domain model classes are in `src/com/kovaciny/primexmodel/` along with `PrimexModel.java` which contains most of the business logic. The 28 fragments and activities in `src/com/kovaciny/linemonitorbot/` are mainly controlled by `MainActivity.java`.

I'm proud of the MVC architecture with views using PropertyChangeListener. I'm not so proud of the "god object" PrimexModel class. I should have modeled the business concerns as objects (eg, a RateCalculator class), instead of modeling only the concrete domain (lines, rolls) and doing mostly procedural operations to them. I had a lot of trouble with things getting into wrong states, when I could have been using stateless helper classes.

The unit tests have some repetition because I hadn't discovered parameterization yet, and many of them test using the UI which makes them kind of fragile, but they work. They catch the kind of bugs that tend to crop up.
