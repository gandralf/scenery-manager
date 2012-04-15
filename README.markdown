Scenery Manager
===============

To develop a template (jsp, velocity, free maker...), you need all-that-messy-server-side stuff up and running. You have to mess with maven, libraries, databases, endless configuration stuff, right?

That means that you are quite dependent. The more dependent, the less freedom.

But, what if we could get the server-side behavior even before it’s done/configured/etc? It would break the dependency!

### Emulating the server side behaviour in a proper MVC

A view (usually a jsp/velocity/free marker/... template) gets some data/objects and displays its content, like

    ...
    <h2>Welcome ${user.name}
    ...

To make it work, you just need to:

1. Write a fake data (file) with the value of all those variables (like user.name). This usually stays in .scn (scenery) files.
2. Merge both files (template and data):
2.1. Define the URL mapping. Each URL combines a template and a scenery data file. Like: for "/home" URL, merges "/home/index.ftl" template and "/home/john.scn" fake data file. This is done in a very simple configuration file in "WEB-INF/scenery.xml"
2.2. Run the scenery manager app and tell it where your webapp is
2.3. Access http://localhost/home to see what happens with your template when it receves such data.

Templates
---------

Scenery manager supports JSP, velocity and freemarker templates. Use includes, macros, whatever.
Just don't do and heavy logic method calls/controller stuff there.
This means that if your template is kind of dumb and just shows information, you are good to go.

For our example, let's asume "/home/index.ftl" (free marker):

    ...
    <#if user??>
        Welcome back, ${user.name}.
    <#else>
        Have an account? <a href="/auth/signin">Sign in</a>. Or <a href="/auth/signup">sign up</a>
    </#if>
    ...
    <div class="footer">
        Hello app, version ${app.version}. Updated at ${app.updatedAt ?datetime}
    </div>
    

Fake data files, aka .scn
-------------------------

You can define the data in a syntax very similar to json. For our example, "/home/john.scn":

    user = {
        name = "John Doe";
    };
    
    app = {
        version = 1.2;
        updatedAt = 2010-02-20;
    };
    
Let's define another scenery ("/home/anonimous.scn"), for someone that isn't logged in, just with the app values:

    app = {
        version = 1.2;
        updatedAt = 2010-02-20;
    };

I know I should use a better syntax, like json itself or haml. But, hey! I did it back in 2002!

### Syntax
It's basically a list of variables and values in the for `name = value;` (don't forget the ";"!), like simple values:

    stringValue = "Some string";
    someReallyAndWeirdString = <![CDATA[
      Here you can do anything, like use ", = and ; at will
    ]]>;
    someNumber = 123;
    evenDates = 2002-07-20;
    
    youCanDefineMapsLikeThis = map: {
        "saturday" = "happy", 
        "sunday"   = "#tenso", 
        "monday"   = "not so great"
    };
    listOfValues = collection: {
        "sun", "mon", "tue", "wed", "thr", "fri", "sat"
    };
    andCombineStuff = map: {
        "hotties" = collection: { "Scarlet", "Kate Upton" },
        "badAsses" = collection: { "Samuel", "Clint" }
    };

URL mapping file, aka 'WEB-INF/scenery.xml'
-------------------------------------------
The first scenery (logged-in user), will be available in "/john" URI. The second one (not logged in), in "/home".

    <scenery-manager>
        <scenery-set uri="/john" template="/home/index.ftl" data="/home/john.scn" />
        <scenery-set uri="/home" template="/home/index.ftl" data="/home/anonymous.scn" />
    </scenery-manager>

Running scenery manager app
---------------------------

Supose that the demo app is located at $HOME/hello-world, and the wabapp source in ./src/main/webapp. Just run:

    cd <scenery-manager-app directory>
    ./run.sh -p $HOME/hello-world/src/main/webapp

Summing up
==========

Scenery manager assigns URLs to a fake data file and a template. And when you access that URL, it “merges” both files and shows you the result.

- Develop and test the view with freedom
- Stress different scenarios
- Minimum overhead
- Useful even when the server-side component is done
- Not a prototype solution, but can be used so
