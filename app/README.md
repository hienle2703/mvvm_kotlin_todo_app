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

4/ Room ? Entity ? RoomDatabase ? 

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