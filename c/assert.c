#include "assert.h"


INT32 VALID(INT32 args, INT32 start, INT32 end)
{
return args>=start&&args<=start;
}
INT32 ASSERT(errorCode returnCode)
{
return (returnCode==OK);
}
