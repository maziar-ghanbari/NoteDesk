# NoteDesk
this is an open source application and its name is NoteDesk.
in this app you can create notes in folders for managing your works and export notes as .txt file or submit notes as notifications in your android phone.
and why I developed this app? as you know in mvvm we can use coroutines and flows, and also we know that Room Database has an advantage which is 
returning Flow in Dao for Observering any changes in SQLite Database, but if we want to observering Room Database in ViewModel we have to remember without
SingleEvent in flow if we call the function that set an Observer(Collector) to the Flow , more than one time; our View will updating more
than one time at the moument! so we need to use SingleEvent.
I was thinking about a challenge which is how to solve this problem without using SingleEvent! if you see ViewModel of MainFragment of this project you will found
tow StateFlows and one LiveData whitch they are solving our problem.
you just have to set a collector to the Flow that is in the Dao one time in ViewModel from Repository, after that you should to collect its data 
and emit its value to one of the stateFlows which its name is DatabaseStateFlow ,after that you should to emit parameters and an enum type which is the type of our
Query filtering; from View to OperatorStateFlow to filtering datas which collected from RoomDatabase in the future.
you should also set for each StateFlows a Collector that is observering their changes in ViewModel, and you should to call a function in these collectors
which mix the operator (which is in OperatorStateFlow) and data (which is in DatabaseStateFlow) to filter datas, and in the last it posts the result to the LiveData 
,this function just need tow parameters that they are data and operator.
so if we set just an observer to our liveData in View, we will observering any changes in Database and also filtering datas as we want!

![scene2](https://user-images.githubusercontent.com/55378791/220334112-f7c820b8-701a-40f0-a6f8-0f40a5daa6bc.png)

this project also have other nice technikal examples alike Extention Functions and permissions managing and...
