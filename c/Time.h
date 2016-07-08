#ifndef _Time_
#define _Time_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
#define MAX_TICKS_VALUE 1000000
#define SECOND_TICKS_VALUE 1000000
#define BREAK_TIME_OUT 60
typedef struct time_info{
INT32 reader;
UINT32 thz;
UINT64 sec;
};
typedef struct timeval{
INT32 tv_sec;
INT32 tv_usec;
};
extern time_info *SysTime;
