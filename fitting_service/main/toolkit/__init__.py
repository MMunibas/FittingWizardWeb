
import sys
import trace
import queue

from threading import Timer, Lock
import types
import threading


def named_lock(name):
    def decorator(func):
        def synced_func(self, *a, **kw):
            if not hasattr(self, name):
                setattr(self, name, threading.Lock())
            lock = getattr(self, name)
            with lock:
                return func(*a, **kw)

        return synced_func

    return decorator


def synchronize_with(lock):
    def synced_obj(obj):
        if isinstance(obj, types.FunctionType):
            obj.__lock__ = lock

            def func(*a, **kw):
                with lock:
                    return obj(*a, **kw)
            return func

        elif isinstance(obj, type):
            real_init = obj.__init__

            def locked_init(self, *a, **kw):
                self.__lock__ = lock
                real_init(self, *a, **kw)
            obj.__init__ = locked_init

            for key in obj.__dict__:
                val = obj.__dict__[key]
                if isinstance(val, types.FunctionType):
                    setattr(obj, key, synchronize_with(lock)(val))
            return obj
    return synced_obj


def synchronized(item):
    if isinstance(item, str):
        return named_lock(item)(item)
    elif type(item) is type(threading.Lock()):
        return synchronize_with(item)(item)
    else:
        lock = threading.Lock()
        return synchronize_with(lock)(item)


class Singleton(type):
    instance = None

    def __init__(cls, name, bases, dict):
        cls._type = type(name, bases, dict)

    def __call__(mcs, *a, **kw):
        if not mcs.instance:
            mcs.instance = mcs._type(*a, **kw)
        return mcs.instance


class RepeatingTimer(object):
    def __init__(self, delay, handler):
        self.delay = delay
        self.handler = handler
        self.running = False
        self.timer = Timer(self.delay, self.handle_tick)

    def start(self):
        self.running = True
        self.timer.start()

    def stop(self):
        self.running = False
        self.timer.cancel()
        self.timer.join()

    def handle_tick(self, *a, **kw):
        self.handler(*a, **kw)
        if self.running:
            self.timer = Timer(self.delay, self.handle_tick)
            self.timer.start()


class TransparentQueue(queue.Queue):
    def list(self):
        with self.mutex:
            return list(self.queue)


class CalculationCanceledException(Exception):
    pass

