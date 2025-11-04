{{- define "orchestrator.name" -}}
{{- default .Chart.Name .Values.nameOverride -}}
{{- end -}}

{{- define "orchestrator.fullname" -}}
{{ printf "%s-%s" (include "orchestrator.name" .) .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{- define "orchestrator.labels" -}}
app: {{ include "orchestrator.name" . }}
chart: "{{ .Chart.Name }}-{{ .Chart.Version }}"
release: {{ .Release.Name }}
heritage: {{ .Release.Service }}
{{- end -}}

{{- define "orchestrator.serviceAccountName" -}}
{{ include "orchestrator.fullname" . }}-sa
{{- end -}}
