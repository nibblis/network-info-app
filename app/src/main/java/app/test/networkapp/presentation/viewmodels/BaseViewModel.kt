package app.test.networkapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<T : Any>(initialState: T) : ViewModel(), CoroutineScope {

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<T> = _state.asStateFlow()

    private val coroutineHandler = CoroutineExceptionHandler { context, exception ->
        println(exception)
    }
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job + coroutineHandler

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}