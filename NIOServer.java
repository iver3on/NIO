/**
 * NIO���Է�����socket����
 * ������������Channel������ServerSocketChannel��SocketChannel������Ҫ��Selectorע�ᣬ
 * ����Selector���������ЩSocket��IO״̬������������һ������Channel���п��õ�IO����ʱ��
 * ��Selector��select()�������᷵�ش���0��������������ֵ�ͱ�ʾ��Selector���ж��ٸ�Channel���п��õ�IO������
 * ���ṩ��selectedKeys()������������ЩChannel��Ӧ��SelectionKey���ϡ�����ͨ��Selector��
 * ʹ�÷�������ֻ��Ҫ���ϵص���Selectorʵ����select()��������֪����ǰ����Channel�Ƿ�����Ҫ�����IO������
��Selector��ע�������Channel��û����Ҫ�����IO����ʱ��select()�����������������ø÷������̱߳�������
 */
package zhangwenbo.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Iver3oN Zhang
 * @date 2016��3��30��
 * @email grepzwb@qq.com NIOServer.java Impossible is nothing
 */
public class NIOServer {
	// ���ڼ������Channel״̬��Selector
	private Selector selector = null;
	// ����ʵ�ֱ��롢������ַ�������
	private Charset charset = Charset.forName("UTF-8");

	public void init() throws IOException {
		selector = Selector.open();
		// ͨ��open��������һ��δ�󶨵�ServerSocketChannelʵ��
		ServerSocketChannel server = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 30000);
		// ����ServerSocketChannel�󶨵�ָ��IP��ַ
		server.socket().bind(isa);
		// ����ServerSocket�Է�������ʽ����
		server.configureBlocking(false);
		// ��serverע�ᵽָ��Selector����
		server.register(selector, SelectionKey.OP_ACCEPT);
		//����������һ������Channel���п��õ�IO����ʱ
		//���÷���ֵ����0ʱ�����Selector�����б�ѡ���SelectionKey��
		while (selector.select() > 0) {
			// ���δ���selector�ϵ�ÿ����ѡ���SelectionKey
			for (SelectionKey sk : selector.selectedKeys()) {
				// ��selector�ϵ���ѡ��Key����ɾ�����ڴ����SelectionKey
				selector.selectedKeys().remove(sk); // ��
				// ���sk��Ӧ��ͨ�������ͻ��˵���������
				if (sk.isAcceptable()) // ��
				{
					// ����accept�����������ӣ������������˶�Ӧ��SocketChannel
					SocketChannel sc = server.accept();
					// ���ò��÷�����ģʽ
					sc.configureBlocking(false);
					// ����SocketChannelҲע�ᵽselector
					sc.register(selector, SelectionKey.OP_READ);
					// ��sk��Ӧ��Channel���ó�׼��������������
					sk.interestOps(SelectionKey.OP_ACCEPT);
				}
				// ���sk��Ӧ��ͨ����������Ҫ��ȡ
				if (sk.isReadable()) // ��
				{
					// ��ȡ��SelectionKey��Ӧ��Channel����Channel���пɶ�������
					SocketChannel sc = (SocketChannel) sk.channel();
					// ����׼��ִ�ж�ȡ���ݵ�ByteBuffer
					ByteBuffer buff = ByteBuffer.allocate(1024);
					String content = "";
					// ��ʼ��ȡ����
					try {
						while (sc.read(buff) > 0) {
							buff.flip();
							content += charset.decode(buff);
						}
						// ��ӡ�Ӹ�sk��Ӧ��Channel���ȡ��������
						System.out.println(sk.toString()+":" + content);
						// ��sk��Ӧ��Channel���ó�׼����һ�ζ�ȡ
						sk.interestOps(SelectionKey.OP_READ);
					}
					// �����׽����sk��Ӧ��Channel�������쳣����������Channel
					// ��Ӧ��Client���������⣬���Դ�Selector��ȡ��sk��ע��
					catch (IOException ex) {
						// ��Selector��ɾ��ָ����SelectionKey
						sk.cancel();
						if (sk.channel() != null) {
							sk.channel().close();
						}
					}
					// ���content�ĳ��ȴ���0����������Ϣ��Ϊ��
					if (content.length() > 0) {
						// ������selector��ע�������SelectKey
						for (SelectionKey key : selector.keys()) {
							// ��ȡ��key��Ӧ��Channel
							Channel targetChannel = key.channel();
							// �����channel��SocketChannel����
							if (targetChannel instanceof SocketChannel) {
								// ������������д���Channel��
								SocketChannel dest = (SocketChannel) targetChannel;
								dest.write(charset.encode(content));
							}
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new NIOServer().init();
	}
}
