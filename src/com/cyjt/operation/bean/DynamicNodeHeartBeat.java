package com.cyjt.operation.bean;

import java.util.Date;

import com.cyjt.operation.base.Tools;

public class DynamicNodeHeartBeat {
	   private Integer id; // 标识号
	    private String nodeId; // 节点编号
	    private Integer carCount; // 车辆数量
	    private Integer occupyTime; // 占用时长
	    private Integer samplingTime; // 采样时长
	    private Integer voltage; // 电压
	    private Integer signal; // 信号量
	    private Integer seq; // 序号
	    private Date submitAt; // 上报时间
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getNodeId() {
			return nodeId;
		}
		public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}
		public Integer getCarCount() {
			return carCount;
		}
		public void setCarCount(Integer carCount) {
			this.carCount = carCount;
		}
		public Integer getOccupyTime() {
			return occupyTime;
		}
		public void setOccupyTime(Integer occupyTime) {
			this.occupyTime = occupyTime;
		}
		public Integer getSamplingTime() {
			return samplingTime;
		}
		public void setSamplingTime(Integer samplingTime) {
			this.samplingTime = samplingTime;
		}
		public Integer getVoltage() {
			return voltage;
		}
		public void setVoltage(Integer voltage) {
			this.voltage = voltage;
		}
		public Integer getSignal() {
			return signal;
		}
		public void setSignal(Integer signal) {
			this.signal = signal;
		}
		public Integer getSeq() {
			return seq;
		}
		public void setSeq(Integer seq) {
			this.seq = seq;
		}
		public Date getSubmitAt() {
			return submitAt;
		}
		public void setSubmitAt(Date submitAt) {
			this.submitAt = submitAt;
		}
		public String getSubmitAtString() {
			if (getSubmitAt() == null) {
				return Tools.getDF(new Date()).toString();
			}
			return Tools.getDF(getSubmitAt()).toString();
		}

		public String getSubmitAtStringFromNow() {
			if (getSubmitAt() != null) {
				String s = Tools.diffTime(getSubmitAt(), new Date());
				if (s.equals("")) {
					return "刚刚";
				}
				return Tools.diffTime(getSubmitAt(), new Date()) + "前";
			}
			return "无提交时间";
		}
}
