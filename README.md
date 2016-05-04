# NIO
NIO 的Demo
/**
 * NIO测试非阻塞socket链接
 * 服务器上所有Channel（包括ServerSocketChannel和SocketChannel）都需要向Selector注册，
 * 而该Selector则负责监视这些Socket的IO状态，当其中任意一个或多个Channel具有可用的IO操作时，
 * 该Selector的select()方法将会返回大于0的整数，该整数值就表示该Selector上有多少个Channel具有可用的IO操作，
 * 并提供了selectedKeys()方法来返回这些Channel对应的SelectionKey集合。正是通过Selector，
 * 使得服务器端只需要不断地调用Selector实例的select()方法即可知道当前所有Channel是否有需要处理的IO操作。
当Selector上注册的所有Channel都没有需要处理的IO操作时，select()方法将被阻塞，调用该方法的线程被阻塞。
 */
