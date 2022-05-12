# MVVM To-Do List App with Flow and Architecture Components

Watch the course here: https://www.youtube.com/playlist?list=PLrnPJCHvNZuCfAe7QK2BoMPkv2TGM_b0E

![thumbnail part 1](https://user-images.githubusercontent.com/52977034/116892669-5afefc80-ac30-11eb-9710-7a927427f02f.png)

---------------

# To-do List with Firebase Real time DB 

Watch the coure here: https://www.youtube.com/playlist?list=PLlkSO32XQLGpF9HzRulWLpMbU3mWZYlJS
![thumbnail firebase](https://i.ytimg.com/vi/zmrPTVR4jJE/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLCqTQt1t8GjN1lJTxrCnSHrf2QJfw)

---------------

MVVM Hierarchy

MVVM: is a programming paradigm used by many developers. Especially for those who working with MXL. This paradigm support two-way binding for Model & ViewModel

View =
- Is the front site of the app to show the UI and handle interaction from users. This part is more fascinating & proactive than other ordinary UI layers, because it
  can do some actions & contact to users through binding, command.

||
||   binding data
||

ViewModel =
- is a replacement for Controller
- contain code that helps Data Binding, Command
- is a class to hold the data for the fragment
- 1VM - many Views (can be 1/2-way binding)

||
||    events & observers
||

Model =
- Is the object layers that help access & operate on real data (from database, data source)

------------------------------------------------------------------------------------------------------------------------------------------------------------

1/ :: ?
- In kotlin, the double colon operator means to take a method as a parameter and pass it to another method for use.
  Generally speaking, it refers to a method.
  => Kiểu kiểu callback nhưng mà là lấy function này nhét vô function kia

2/ DAO ?
- DAO = Data Access Object: là một interface định nghĩa các phương thức trừu tượng việc triển khai truy cập dữ liệu cơ bản cho BusinessObject
  để cho phép truy cập vào nguồn dữ liệu (DataSource).
  *interface: giao diện / khai báo. chỉ là class giữ nhiệm vụ khai báo chứ ko có định nghĩa.

3/ Dagger ? Hilt ?
- Dagger là 1 dependency injection framework, dùng để generate code thông qua base annotation (chú thích) và để quản lý các dependencies
- Có thể hiểu Dependency Injection một cách đơn giản như sau:
  Các module không giao tiếp trực tiếp với nhau, mà thông qua interface. 
  Module cấp thấp sẽ implement interface, module cấp cao sẽ gọi module cấp thấp thông qua interface.

- Tuy nhiên thì Dagger khá rộng và nặng => Dagger-Hilt là 1 phần của thư viện . Chia thành 4 phần:
    + Module: Provider
    + Provides: Provider
    + Component: Bridge
    + Inject: 
  
4/ Room ? @Entity ? RoomDatabase ?
- Room Database

- @Entity: create a SQLite table using data model 
- @Dao: create a Data Access Object in the db using interface class
- @Database: A class with this annotation will create an abstraction for the DAO

5/ Inject? Singleton?

6/ Coroutines Flow?

7/ Annotation?
- Chú thích / metadata để cấp thông tin dữ liệu cho mã nguồn

8/ Constructor?

9/ ViewModel() ? @ViewModalInject ?

10/ @AndroidEntryPoint?

11/ @HiltAndroidApp?

12/ Application() from android.app.Application?

13/ RecyclerView? RecyclerViewAdapter? ListAdapter?

14/ binding? inflate?
- binding: view binding giúp tối giản code, thay thế cho findViewById
- inflater, inflate: dùng để biến code XML thành View(Java code) trong android

15/ asLiveData()

16/ lambda arguement?

17/  '%' || :searchQuery || '%'  in SQLite ?

18/ MutableStateFlow? flatMapLatest?

19/ Combine multiple Flow?

20/ Jetpack Datastore?

21/ IOException?

22/ viewModelScope.launch?

23/ viewLifecycleOwner.lifecycleScope.launch?

24/ init in Kotlin? interface?

25/ position? adapterPosition?

26/ inner class

27/ onClickListener? listener?

28/ ItemTouchHelper?

29/ attachToRecyclerView?

30/ sealed class?

31/ Channel in coroutines?

32/ SavedStateHandle?

33/ @Assisted?

34/ asFlow()?

35/ NavController? NavHostFragment?

36/ setupActionBarWithNavController()? onSupportNavigateUp()?

37/ Activity.ADD_TASK_RESULT_OK?

38/ setFragmentResult() ?

39/ setFragmentResult()?

40/ DialogFragment()?

41/ android vs androidx

42/ expandActionView?