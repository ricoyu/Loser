package com.loserico.message;

/**
 * @of
 * Destination高级特性之Wildcards
 * 
 * Wildcards用来支持名字分层体系，它不是JMS规范的一部分，是ActiveMQ的扩展。
 * ActiveMQ支持以下三种wildcards: 
 * 1:"."	用于作为路径上名字间的分隔符 
 * 2:"*" 	用于匹配路径上的任何名字(就一级层次，不会递归)
 * 3:">" 	用于递归地匹配任何以这个名字开始的destination(可以递归地匹配多级层次)
 * 
 * 示例，设想你有如下两个destinations 
 * PRICE.STOCK.NASDAQ.IBM (IBM在NASDAQ的股价)
 * PRICE.STOCK.NYSE.SUNW (SUN在纽约证券交易所的股价) 
 * 
 * 那么: 
 * 
 * 1:PRICE.> 	匹配任何产品的价格变动(以PRICE.开头)
 * 2:PRICE.STOCK.>	匹配任何产品的股票价格变动 
 * 3:PRICE.STOCK.NASDAQ.*	匹配任何在NASDAQ下面的产品的股票价格变动
 * 4:PRICE.STOCK.*.IBM		匹配任何IBM的产品的股票价格变动 
 * 
 * 客户化路径分隔符，比如你想要用"/" 来替换"." 
 * <plugins>
 * 		<destinationPathSeparatorPlugin/> 
 * </plugins>
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-08 11:43
 * @version 1.0
 *
 */
public class DestinationWildcardTest {

}
